package com.wincovid21.ingestion.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "resource_availability_details")
@Data
public class ResourceAvailabilityDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "category")
    private String category;

    @Column(name = "resource_type")
    private String resourceType;

    @Column(name = "is_available")
    private boolean isAvailable;

}
