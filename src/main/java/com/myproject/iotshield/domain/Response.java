package com.myproject.iotshield.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.myproject.iotshield.domain.Action;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Response {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String deviceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String requestId;
    private Action action;
}
