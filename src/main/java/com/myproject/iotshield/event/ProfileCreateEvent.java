package com.myproject.iotshield.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myproject.iotshield.domain.Policy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCreateEvent extends Event{
    private EventType type;
    private String modelName;
    @JsonProperty("default")
    private Policy defaultPolicy;
    private List<String> whitelist;
    private List<String> blacklist;
    private Instant timestamp;
}
