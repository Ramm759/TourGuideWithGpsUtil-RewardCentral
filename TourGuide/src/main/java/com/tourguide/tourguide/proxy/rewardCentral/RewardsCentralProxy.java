package com.tourguide.tourguide.proxy.rewardCentral;

import com.tourguide.tourguide.proxy.gpsUtil.dto.Attraction;
import com.tourguide.tourguide.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.CompletableFuture;

@FeignClient(name = "rewardCentral", url = "localhost:8082") // déclare l'application mère et l'enfant
public interface RewardsCentralProxy {
    @RequestMapping("/indexReward")
    public String indexReward();

    @GetMapping("/getAttractionRewardPoints")
    public CompletableFuture<Integer> getAttractionRewardPoints(@RequestParam Attraction attraction, User user);
}
