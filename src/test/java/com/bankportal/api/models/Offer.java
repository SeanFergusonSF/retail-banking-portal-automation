package com.bankportal.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Offer {
    private String offerId;
    private String productId;
    private String description;
    private boolean eligible;
}