package com.tourguide.tourguide;

import com.tourguide.tourguide.proxy.gpsUtil.GpsUtilProxy;
import com.tourguide.tourguide.proxy.rewardCentral.RewardsCentralProxy;
import com.tourguide.tourguide.service.RewardsService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class TourGuideModule {

    @Bean
    public RewardsService getRewardsService(GpsUtilProxy gpsUtilService, RewardsCentralProxy rewardsCentralService) {
        return new RewardsService(gpsUtilService, rewardsCentralService);

    }

}
