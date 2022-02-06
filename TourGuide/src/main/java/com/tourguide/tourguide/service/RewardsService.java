package com.tourguide.tourguide.service;


import com.tourguide.tourguide.proxy.gpsUtil.GpsUtilProxy;
import com.tourguide.tourguide.proxy.gpsUtil.dto.Attraction;
import com.tourguide.tourguide.proxy.gpsUtil.dto.Location;
import com.tourguide.tourguide.proxy.gpsUtil.dto.VisitedLocation;
import com.tourguide.tourguide.proxy.rewardCentral.RewardsCentralProxy;
import com.tourguide.tourguide.user.User;
import com.tourguide.tourguide.user.UserReward;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private final GpsUtilProxy gpsUtilService;
    private final RewardsCentralProxy rewardsCentralService;
    // proximity in miles
    private int defaultProximityBuffer = 10;
    private int proximityBuffer = defaultProximityBuffer;
    private int attractionProximityRange = 200;

    public RewardsService(GpsUtilProxy gpsUtilService, RewardsCentralProxy rewardsCentralService) {
        this.gpsUtilService = gpsUtilService;
        this.rewardsCentralService = rewardsCentralService;
    }

    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    public void setDefaultProximityBuffer() {
        proximityBuffer = defaultProximityBuffer;
    }

    public CompletableFuture<Void> calculateRewards(User user) {
        // Correction bug sur liste endroits visités (concurent)
        List<VisitedLocation> userLocations = new ArrayList<>(user.getVisitedLocations());
        List<Attraction> attractions = gpsUtilService.getAttractions();

        // on a besoin de 2 choses, la liste est remplacée
        //List<CompletableFuture<Void>> rewardPoints = new ArrayList<>();

        // Pour construire une reward pour l'utilisateur, on a besoin de 2 choses : les rewardPoints et la localisation associée
        // On collecte les résultats des appels asynchrones pour chaque attraction
        Map<Attraction, Integer> rewardPoints = new HashMap<>();

        // On collecte la localisation de l'utilisateur pour chaque attraction
        Map<Attraction, VisitedLocation> rewardVisitedLocations = new HashMap<>();

        // On collecte les appels asynchrones pour chaque attraction
        Map<Attraction, CompletableFuture<Void>> rewardFutures = new HashMap<>();

        // on parcoure les lieux visités par l'utilisateur
        for (VisitedLocation visitedLocation : userLocations) {
            // Liste des attractions à visiter (GPS)
            for (Attraction attraction : attractions) {
                // On vérifie si l'utilisateur n'a pas déjà eu la récompense
                if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
                    // Pour chaque attraction on vérifie que l'utilsateur est proche de l'attraction
                    if (nearAttraction(visitedLocation, attraction)) {
                        // si oui, récompense attribuée

                        // Plusieurs Thread pour la méthode addUserReward
                        rewardFutures.putIfAbsent(attraction, rewardsCentralService.getAttractionRewardPoints(attraction, user).thenAccept(rewardPoint -> rewardPoints.put(attraction, rewardPoint)));
                        rewardVisitedLocations.putIfAbsent(attraction, visitedLocation);

                    }
                }
            }
        }

        return CompletableFuture.allOf(rewardFutures.values().toArray(new CompletableFuture[0])).thenAccept(v -> rewardPoints.forEach((attraction, rewardPoint) -> user.addUserReward(new UserReward(rewardVisitedLocations.get(attraction), attraction, rewardPoint))));
    }

    // Correction du test : on vérifie la distance entre l'attraction et l'utilisateur
    public boolean isWithinAttractionProximity(double distance) {
        return distance <= attractionProximityRange;
    }

    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return getDistance(attraction, location) > attractionProximityRange ? false : true;
    }

    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
    }

    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
    }

    public void setDefaultProximityBuffer(int defaultProximityBuffer) {
        this.defaultProximityBuffer = defaultProximityBuffer;
    }

    public void setAttractionProximityRange(int attractionProximityRange) {
        this.attractionProximityRange = attractionProximityRange;
    }
}
