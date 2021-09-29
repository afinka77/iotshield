package com.myproject.iotshield.service;

import com.myproject.iotshield.domain.Action;
import com.myproject.iotshield.domain.Policy;
import com.myproject.iotshield.domain.Response;
import com.myproject.iotshield.event.ProfileUpdateEvent;
import com.myproject.iotshield.event.RequestEvent;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private Map<String, DeviceStats> deviceStatsMap = new HashMap<>();

    public void addRequestStatistics(RequestEvent requestEvent, Response response) {
        DeviceStats deviceStats = deviceStatsMap.get(requestEvent.getDeviceId());
        if (deviceStats == null) {
            deviceStats = new DeviceStats(requestEvent.getDeviceId(), requestEvent.getModelName());
            deviceStatsMap.put(requestEvent.getDeviceId(), deviceStats);
        }

        if (Action.ALLOW.equals(response.getAction())) {
            deviceStats.getAllowedUrls().add(requestEvent.getUrl());
        } else {
            deviceStats.getBlockedUrls().add(requestEvent.getUrl());
        }
    }

    public void addProfileUpdateStatistics(ProfileUpdateEvent profileUpdateEvent, Policy policy) {
        deviceStatsMap.values().stream()
                .filter(d -> profileUpdateEvent.getModelName().equals(d.getModelName()))
                .forEach(d -> updateDeviceStats(d, profileUpdateEvent, policy));
    }

    private void updateDeviceStats(DeviceStats deviceStats, ProfileUpdateEvent profileUpdateEvent, Policy policy) {
        if (Policy.ALLOW.equals(policy)) {
            Long badUrlsCount = deviceStats.getBlockedUrls().stream()
                    .filter(url -> profileUpdateEvent.getBlacklist().contains(url))
                    .count();
            deviceStats.setIssuedIncorrectlyBlocks(deviceStats.issuedIncorrectlyBlocks + badUrlsCount);
        } else if (Policy.BLOCK.equals(policy)) {
            Long missedUrlsCount = deviceStats.getAllowedUrls().stream()
                    .filter(url -> profileUpdateEvent.getWhitelist().contains(url))
                    .count();
            deviceStats.setMissedBlocks(deviceStats.getMissedBlocks() + missedUrlsCount);
        }
    }

    public void printStatistics() {
        System.out.println("\n- Statistics:");
        System.out.println(" - How many devices were protected by our solution: " +
                deviceStatsMap.size());
        System.out.println(" - How many devices are suspected to be hacked: " +
                deviceStatsMap.values().stream()
                        .filter(d -> d.getBlockedUrls().size() > 0)
                        .count());
        System.out.println(" - How many blocks were missed due to delayed profile updates: " +
                deviceStatsMap.values().stream()
                        .map(DeviceStats::getMissedBlocks)
                        .reduce(Long::sum));
        System.out.println(" - How many blocks were issued incorrectly due to delayed profile updates? " +
                deviceStatsMap.values().stream()
                        .map(DeviceStats::getIssuedIncorrectlyBlocks)
                        .reduce(Long::sum));
    }

    @Data
    class DeviceStats {
        private String deviceId;
        private String modelName;
        private List<String> allowedUrls = new ArrayList<>();
        private List<String> blockedUrls = new ArrayList<>();
        private Long missedBlocks = 0L;
        private Long issuedIncorrectlyBlocks = 0L;

        public DeviceStats(String deviceId, String modelName) {
            this.deviceId = deviceId;
            this.modelName = modelName;
        }
    }
}
