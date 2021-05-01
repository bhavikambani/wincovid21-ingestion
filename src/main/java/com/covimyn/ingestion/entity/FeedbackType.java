package com.covimyn.ingestion.entity;

import lombok.Data;

import javax.persistence.*;

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
}
