package com.wincovid21.ingestion.entity;


import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "resource_details")
@Data
public class ResourceDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "category")
    private ResourceCategory category;

    @ManyToOne
    @JoinColumn(name = "resource_type")
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

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @Column(name = "quantity_available")
    private Long quantityAvailable;

    @Column(name = "price")
    private Double price;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private Long createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private Long updatedOn;

    @Column(name = "is_verified")
    private boolean isVerified;

}