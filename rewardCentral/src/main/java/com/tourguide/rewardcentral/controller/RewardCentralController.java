package com.tourguide.rewardcentral.controller;

import com.tourguide.rewardcentral.proxy.gpsUtil.GpsUtilProxy;
import com.tourguide.rewardcentral.proxy.gpsUtil.dto.Attraction;
import com.tourguide.rewardcentral.proxy.gpsUtil.dto.user.User;
import com.tourguide.rewardcentral.service.RewardCentralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class RewardCentralController {
    @Autowired
    GpsUtilProxy gpsUtilProxy;

    @Autowired
    RewardCentralService rewardCentralService;

    @RequestMapping("/indexReward")
    public String indexReward() {
        return "Greetings from RewardCentral";
    }

    // Méthodes de GpsUtil
    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return gpsUtilProxy.getAttractions();
    }


    @GetMapping("/getAttractionRewardPoints")
    public CompletableFuture<Integer> getAttractionRewardPoints(@RequestParam Attraction attraction, User user) {
        return rewardCentralService.getAttractionRewardPoints(attraction, user);
    }
}
