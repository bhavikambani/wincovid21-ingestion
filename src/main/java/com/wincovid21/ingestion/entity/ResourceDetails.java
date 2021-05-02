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

    @Column(name = "category")
    private String category;

    @Column(name = "resource_type")
    private String resourceType;

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

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "quantity_available")
    private String quantityAvailable;

    @Column(name = "price")
    private String price;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    private Long createdOn;

    @Column(name = "updated_on")
    private Long updatedOn;

    @Column(name = "is_verified")
    private boolean isVerified;

}
