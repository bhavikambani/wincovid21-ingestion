package com.wincovid21.ingestion.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "feedback_types")
@Data
public class FeedbackType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "feedback_code")
    private String feedbackCode;

    @Column(name = "feedback_message")
    private String feedbackMessage;

    @OneToMany(mappedBy = "feedbackType", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<UserTypeAllowedFeedbackTypes> allowedFeedbackTypes;


}
