package com.wincovid21.ingestion.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequestEntry {

    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private Long category;
    @NonNull
    private Long subcategory;
    private String address;
    private String pincode;
    private String description;
    @NonNull
    private String phone1;
    private String phone2;
    private String email;
    @NonNull
    private Long city;
    @NonNull
    private Long state;
    @NonNull
    private boolean isAvailable;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    @NonNull
    private String updatedAt;
    @NonNull
    private boolean isVerified;

}
