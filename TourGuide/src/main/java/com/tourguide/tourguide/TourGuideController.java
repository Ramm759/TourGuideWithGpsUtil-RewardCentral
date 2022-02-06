package com.tourguide.tourguide;

import com.jsoniter.output.JsonStream;
import com.tourguide.tourguide.proxy.gpsUtil.GpsUtilProxy;
import com.tourguide.tourguide.proxy.gpsUtil.dto.Attraction;
import com.tourguide.tourguide.proxy.gpsUtil.dto.Location;
import com.tourguide.tourguide.proxy.gpsUtil.dto.VisitedLocation;
import com.tourguide.tourguide.proxy.rewardCentral.RewardsCentralProxy;
import com.tourguide.tourguide.service.TourGuideService;
import com.tourguide.tourguide.user.User;
import com.tourguide.tourguide.user.UserPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tripPricer.Provider;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @Autowired
    RewardsCentralProxy rewardCentralProxy;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    // Méthode de RewardCentral
    @RequestMapping("/indexReward")
    public String indexReward() {
        return rewardCentralProxy.indexReward();
    }

    @GetMapping("/getAttractionRewardPoints")
    public CompletableFuture<Integer> getAttractionRewardPoints(@RequestParam Attraction attraction, User user) {
        return rewardCentralProxy.getAttractionRewardPoints(attraction, user);
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the attractions.
    // The reward points for visiting each Attraction.
    //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocation));
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public Map<UUID, Location> getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }

        return tourGuideService.getAllUsersLocations();
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }

    @RequestMapping("/setUserPreferences")
    public String setUserPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences) {
        return JsonStream.serialize(tourGuideService.setUserPreferences(getUser(userName), userPreferences));

    }

    @RequestMapping("/getUserPreferences")
    public String getUserPreferences(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserPreferences(getUser(userName)));

    }


}