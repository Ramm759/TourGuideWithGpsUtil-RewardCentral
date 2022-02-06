package com.tourguide.rewardcentral;

import com.tourguide.rewardcentral.service.RewardCentralService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients
public class RewardCentralModule {

    @Bean
    public RewardCentralService getRewardCentralService() {
        return new RewardCentralService();
    }

}
