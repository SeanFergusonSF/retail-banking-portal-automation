package com.bankportal.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String customerId;
    private String name;
    private String segment;
    private Map<String, Boolean> eligibilityFlags;
}