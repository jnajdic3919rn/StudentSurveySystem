package com.example.demo.model.domain.survey;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="subject_for_survey")
@Getter
@Setter
public class SubjectForSurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Survey survey;
    @ManyToOne
    private Subject subject;
    private double grade;
    private int votes;

}
