package com.myproject.iotshield.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEvent extends Event{
    private EventType type;
    private String requestId;
    private String modelName;
    private String deviceId;
    private String url;
    private Instant timestamp;
}
