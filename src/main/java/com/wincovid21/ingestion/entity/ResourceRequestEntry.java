package com.wincovid21.ingestion.entity;

import lombok.Data;

@Data
public class ResourceRequestEntry {

    private String id;
    private String name;
    private String comment;
    private String address;
    private String email;
    private String pincode;
    private String city;
    private String state;
    private String phone1;
    private String phone2;
    private boolean isVerified;
    private String resourceType;
    private boolean isAvailable;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
}
