package com.myproject.iotshield;

import com.myproject.iotshield.service.IoTShieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IoTShieldApp implements CommandLineRunner {
    private IoTShieldService service;

    public IoTShieldApp(@Autowired IoTShieldService service){
        this.service = service;
    }

    public static void main(String[] args) {
        SpringApplication.run(IoTShieldApp.class, args);
    }

    @Override
    public void run(String... args) {
        service.handleEvents();
    }
}
