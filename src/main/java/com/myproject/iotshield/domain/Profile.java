package com.myproject.iotshield.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
public class Profile {
    private String modelName;
    private Policy defaultPolicy;
    private List<String> whitelist;
    private List<String> blacklist;
}
