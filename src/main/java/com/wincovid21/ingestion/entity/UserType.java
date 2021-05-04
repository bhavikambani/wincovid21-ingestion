package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_type")
@Data
public class UserType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "user_type", nullable = false, unique = true, length = 128)
    private String userType;

    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private Date updatedOn;

    @OneToMany(mappedBy = "userType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<UserDetails> users;

    @OneToMany(mappedBy = "userType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<UserTypeAllowedFeedbackTypes> allowedFeedbackTypes;

}
