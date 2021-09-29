package com.myproject.iotshield;

import com.myproject.iotshield.service.IoTShieldService;
import com.myproject.iotshield.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IoTShieldApp implements CommandLineRunner {
    private IoTShieldService ioTShieldService;
    private StatisticsService statisticsService;

    public IoTShieldApp(@Autowired IoTShieldService ioTShieldService,
                        @Autowired StatisticsService statisticsService){
        this.ioTShieldService = ioTShieldService;
        this.statisticsService = statisticsService;
    }

    public static void main(String[] args) {
        SpringApplication.run(IoTShieldApp.class, args);
    }

    @Override
    public void run(String... args) {
        ioTShieldService.handleEvents();
        statisticsService.printStatistics();
    }
}
