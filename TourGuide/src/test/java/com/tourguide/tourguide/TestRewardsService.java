package com.tourguide.tourguide;

import static org.junit.Assert.*;

import com.tourguide.tourguide.helper.InternalTestHelper;
import com.tourguide.tourguide.proxy.gpsUtil.GpsUtilProxy;
import com.tourguide.tourguide.proxy.gpsUtil.dto.Attraction;
import com.tourguide.tourguide.proxy.gpsUtil.dto.VisitedLocation;
import com.tourguide.tourguide.proxy.rewardCentral.RewardsCentralProxy;
import com.tourguide.tourguide.service.RewardsService;
import com.tourguide.tourguide.service.TourGuideService;
import com.tourguide.tourguide.user.User;
import com.tourguide.tourguide.user.UserReward;
import org.junit.Before;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;


public class TestRewardsService {
    GpsUtilProxy gpsUtilService = Mockito.mock(GpsUtilProxy.class);
    RewardsCentralProxy rewardsCentralService = Mockito.mock(RewardsCentralProxy.class);

    @Before
    public void init() {
        List<Attraction> attractions = new ArrayList();
        attractions.add(new Attraction("Disneyland", "Anaheim", "CA", 33.817595D, -117.922008D));
        attractions.add(new Attraction("Jackson Hole", "Jackson Hole", "WY", 43.582767D, -110.821999D));
        attractions.add(new Attraction("Mojave National Preserve", "Kelso", "CA", 35.141689D, -115.510399D));
        attractions.add(new Attraction("Joshua Tree National Park", "Joshua Tree National Park", "CA", 33.881866D, -115.90065D));
        attractions.add(new Attraction("Buffalo National River", "St Joe", "AR", 35.985512D, -92.757652D));
        attractions.add(new Attraction("Hot Springs National Park", "Hot Springs", "AR", 34.52153D, -93.042267D));
        Mockito.when(gpsUtilService.getAttractions()).thenReturn(attractions);
    }

    @Disabled
    @Test
    public void monTest(){
        List<Attraction> attractionList = gpsUtilService.getAttractions();
        Attraction attraction = attractionList.get(0);
        assertTrue(attraction.attractionName == "Disneyland");
        System.out.println(attraction.attractionName);
    }

    @Test
    public void userGetRewards() {
        RewardsService rewardsService = new RewardsService(gpsUtilService, rewardsCentralService);

        InternalTestHelper.setInternalUserNumber(0);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Attraction> attractionList = gpsUtilService.getAttractions();

        Attraction attraction = gpsUtilService.getAttractions().get(0);

        user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));
        tourGuideService.trackUserLocation(user).join();
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();
        assertTrue(userRewards.size() == 1);
    }

    @Test
    public void isWithinAttractionProximity() {

        RewardsService rewardsService = new RewardsService(gpsUtilService, rewardsCentralService);
        Attraction attraction = gpsUtilService.getAttractions().get(0);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    // Needs fixed - can throw ConcurrentModificationException
    @Test
    public void nearAllAttractions() {
        RewardsService rewardsService = new RewardsService(gpsUtilService, rewardsCentralService);
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);

        InternalTestHelper.setInternalUserNumber(1);
        TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

        rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0)).join();

        List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
        tourGuideService.tracker.stopTracking();

        assertEquals(gpsUtilService.getAttractions().size(), userRewards.size());
    }
}
