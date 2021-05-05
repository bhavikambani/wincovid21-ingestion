package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_session")
@Data
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private UserDetails user;

    @Column(name = "token_id", updatable = false, unique = true, nullable = false)
    private String tokenId;

    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @Transient
    private String name;


}
