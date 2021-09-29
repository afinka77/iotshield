package com.myproject.iotshield.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.iotshield.domain.Action;
import com.myproject.iotshield.domain.Policy;
import com.myproject.iotshield.domain.Profile;
import com.myproject.iotshield.domain.Response;
import com.myproject.iotshield.event.ProfileCreateEvent;
import com.myproject.iotshield.event.ProfileUpdateEvent;
import com.myproject.iotshield.event.RequestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class IoTShieldService {

    private ParserService parserService;
    private StatisticsService statisticsService;
    private ObjectMapper mapper;
    private Map<String, Profile> profileMap = new HashMap<>();

    public IoTShieldService(@Autowired ParserService parserService,
                            @Autowired StatisticsService statisticsService,
                            @Autowired ObjectMapper mapper) {
        this.parserService = parserService;
        this.statisticsService = statisticsService;
        this.mapper = mapper;
    }

    public void handleEvents() {
        parserService.getEventsProducer().subscribe(e -> {
            switch (e.getClass().getSimpleName()) {
                case "ProfileCreateEvent" -> createProfile((ProfileCreateEvent) e);
                case "ProfileUpdateEvent" -> updateProfile((ProfileUpdateEvent) e);
                case "RequestEvent" -> handleRequestEvent((RequestEvent) e);
            }
        });
    }

    private void handleRequestEvent(RequestEvent requestEvent) {
        Response response = getResponse(requestEvent);
        statisticsService.addRequestStatistics(requestEvent, response);
        printResponse(response);
    }

    private Response getResponse(RequestEvent requestEvent) {
        String modelName = requestEvent.getModelName();
        Profile profile = profileMap.get(modelName);
        Action action = profile != null ? getAction(profile, requestEvent) : Action.ALLOW;
        return Response.builder()
                .requestId(Action.QUARANTINE.equals(action) ? null : requestEvent.getRequestId())
                .deviceId(Action.QUARANTINE.equals(action) ? requestEvent.getDeviceId() : null)
                .action(action)
                .build();
    }

    private void createProfile(ProfileCreateEvent profileCreateEvent) {
        profileMap.put(profileCreateEvent.getModelName(),
                Profile.builder()
                        .modelName(profileCreateEvent.getModelName())
                        .defaultPolicy(profileCreateEvent.getDefaultPolicy())
                        .blacklist(profileCreateEvent.getBlacklist())
                        .whitelist(profileCreateEvent.getWhitelist()).
                        build());
    }

    private void updateProfile(ProfileUpdateEvent profileUpdateEvent) {
        Profile profile = profileMap.get(profileUpdateEvent.getModelName());
        if (profile != null) {
            if (Policy.ALLOW.equals(profile.getDefaultPolicy())) {
                profile.setBlacklist(Optional.ofNullable(profileUpdateEvent.getBlacklist()).orElse(new ArrayList<>()));
            }
            if (Policy.BLOCK.equals(profile.getDefaultPolicy())) {
                profile.setWhitelist(Optional.ofNullable(profileUpdateEvent.getWhitelist()).orElse(new ArrayList<>()));
            }
            profileMap.put(profileUpdateEvent.getModelName(), profile);
            statisticsService.addProfileUpdateStatistics(profileUpdateEvent, profile.getDefaultPolicy());
        }
    }

    private Action getAction(Profile profile, RequestEvent requestEvent) {
        if (Policy.BLOCK.equals(profile.getDefaultPolicy())) {
            if (!profile.getWhitelist().contains(requestEvent.getUrl())) {
                return Action.QUARANTINE;
            }
        } else if (profile.getBlacklist().contains(requestEvent.getUrl())) {
            return Action.BLOCK;
        }

        return Action.ALLOW;
    }

    private void printResponse(Response response) {
        try {
            System.out.println(mapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            throw new ApplicationContextException("Error while writing response", e);
        }
    }
}
