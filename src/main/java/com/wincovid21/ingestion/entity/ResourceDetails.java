package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.lang.annotation.Native;

@Entity
@Table(name = "resource_details")
@Data
public class ResourceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    @ToString.Exclude
    private ResourceCategory category;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_type")
    @ToString.Exclude
    private ResourceSubCategory resourceType;

    @Column(name = "address")
    private String address;

    @Column(name = "pin_code")
    private Long pinCode;

    @Column(name = "description")
    private String description;

    @Column(name = "phone_1")
    private String phone1;

    @Column(name = "phone_2")
    private String phone2;

    @Column(name = "email")
    private String email;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "city")
    @ToString.Exclude
    private City city;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "state")
    @ToString.Exclude
    private State state;

    @Column(name = "quantity_available")
    private String quantityAvailable;

    @Column(name = "price")
    private String price;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private Long createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private Long updatedOn;

    @Column(name = "is_verified")
    private boolean isVerified;
}

