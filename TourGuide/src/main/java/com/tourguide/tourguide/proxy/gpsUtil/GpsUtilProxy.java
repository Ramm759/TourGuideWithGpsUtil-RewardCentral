package com.tourguide.tourguide.proxy.gpsUtil;

import com.tourguide.tourguide.proxy.gpsUtil.dto.Attraction;
import com.tourguide.tourguide.proxy.gpsUtil.dto.VisitedLocation;
import com.tourguide.tourguide.user.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@FeignClient(name = "gpsUtil", url = "localhost:8081") // déclare l'application mère et l'enfant
public interface GpsUtilProxy {
    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();

    @RequestMapping("/getUserLocation")
    CompletableFuture<VisitedLocation> getUserLocation(@RequestParam UUID userId);

    default CompletableFuture<VisitedLocation> getUserLocation(User user) {
        return getUserLocation(user.getUserId());
    }
}
