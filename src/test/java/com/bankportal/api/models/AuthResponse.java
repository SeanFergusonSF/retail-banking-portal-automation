package com.bankportal.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    private String token;
    private String expiry;
    private List<String> roles;
}