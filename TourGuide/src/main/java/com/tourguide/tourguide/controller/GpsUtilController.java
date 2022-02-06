package com.tourguide.tourguide.controller;

import com.tourguide.tourguide.proxy.gpsUtil.GpsUtilProxy;
import com.tourguide.tourguide.proxy.gpsUtil.dto.Attraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GpsUtilController {

    @Autowired
    GpsUtilProxy gpsUtilProxy;

    // Méthodes de GpsUtil
    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return gpsUtilProxy.getAttractions();
    }

}
