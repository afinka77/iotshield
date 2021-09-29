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
import reactor.core.publisher.Flux;

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
        printResponses(getResponses(requestEvent));
    }

    private Flux<Response> getResponses(RequestEvent requestEvent) {
        Flux<Response> flux = Flux.just();
        String modelName = requestEvent.getModelName();
        Profile profile = profileMap.get(modelName);
        if (profile != null) {
            Action action = getAction(profile, requestEvent);
            Response response = Response.builder()
                    .requestId(Action.QUARANTINE.equals(action) ? null : requestEvent.getRequestId())
                    .deviceId(Action.QUARANTINE.equals(action) ? requestEvent.getDeviceId() : null)
                    .action(getAction(profile, requestEvent))
                    .build();
            flux = Flux.concat(flux, Flux.just(response));
        }
        return flux;
    }

    private void printResponses(Flux<Response> flux) {
        flux.subscribe(this::printJsonAsString);
    }

    private void printJsonAsString(Object json) {
        try {
            System.out.println(mapper.writeValueAsString(json));
        } catch (JsonProcessingException e) {
            throw new ApplicationContextException("Error while writing response", e);
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
        }
    }
}
