package com.myproject.iotshield.service;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    public void printStatistics(){
        System.out.println("\n- Statistics:");
        System.out.println(" - How many devices were protected by our solution?");
        System.out.println(" - How many devices are suspected to be hacked?");
        System.out.println(" - How many blocks were missed due to delayed profile updates?");
        System.out.println(" - How many blocks were issued incorrectly due to delayed profile updates?");
    }
}
