package com.myproject.iotshield.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateEvent extends Event{
    private EventType type;
    private String modelName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> whitelist;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> blacklist;
    private Instant timestamp;
}
