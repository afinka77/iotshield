package com.myproject.iotshield.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.iotshield.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ParserService {

    private static final String TYPE_NODE = "type";

    private Flux<String> fluxOfStrings;
    private ObjectMapper mapper;

    public ParserService(@Autowired Flux<String> fluxOfStrings,
                         @Autowired ObjectMapper mapper) {
        this.fluxOfStrings = fluxOfStrings;
        this.mapper = mapper;
    }

    public Flux<Event> getEventsProducer() {
        return fluxOfStrings.map(this::getEvent);
    }

    private JsonNode getJsonNode(String line) throws JsonProcessingException {
        return mapper.readTree(line);
    }

    private EventType getEventType(JsonNode node) {
        return EventType.valueOf(node.get(TYPE_NODE).asText().toUpperCase());
    }

    private Event getEvent(String line) {
        try {
            JsonNode node = getJsonNode(line);
            return switch (getEventType(node)) {
                case REQUEST -> mapper.treeToValue(node, RequestEvent.class);
                case PROFILE_CREATE -> mapper.treeToValue(node, ProfileCreateEvent.class);
                case PROFILE_UPDATE -> mapper.treeToValue(node, ProfileUpdateEvent.class);
            };
        } catch (JsonProcessingException e) {
            throw new ApplicationContextException("Error while processing string representation of evenr", e);
        }
    }
}
