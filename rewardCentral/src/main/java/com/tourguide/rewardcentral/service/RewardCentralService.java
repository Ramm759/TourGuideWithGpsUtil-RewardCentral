package com.tourguide.rewardcentral.service;

import com.tourguide.rewardcentral.proxy.gpsUtil.dto.Attraction;
import com.tourguide.rewardcentral.proxy.gpsUtil.dto.user.User;
import rewardCentral.RewardCentral;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RewardCentralService {
    private RewardCentral rewardCentral = new RewardCentral();
    // on autorise 10000 Threads
    private ExecutorService executorService = Executors.newFixedThreadPool(10000);

    public CompletableFuture<Integer> getAttractionRewardPoints(Attraction attraction, User user) {

        // getAttractionRewardPoints peut s'executer sur plusieurs threads
        return CompletableFuture.supplyAsync(() -> rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()), executorService);
    }

}
