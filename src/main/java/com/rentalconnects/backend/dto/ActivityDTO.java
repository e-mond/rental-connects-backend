package com.rentalconnects.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivityDTO {
    private String id;
    private String type;
    private String message;
    private String time;
    private String entityId;
    private String propertyId;
    private String propertyName;
}