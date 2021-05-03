package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "user_details")
@Data
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "user_name", nullable = false, unique = true, length = 128)
    private String userName;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @Column(name = "updated_on", insertable = false, updatable = false)
    private Date updatedOn;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<UserSession> userSessions;


}
