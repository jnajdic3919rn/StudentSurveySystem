package com.example.demo.model.domain.survey;

import com.example.demo.model.constants.Label;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="comment", indexes = {@Index(columnList = "label")})
@Getter
@Setter
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(columnDefinition = "TEXT")
  private String text;
  @Enumerated(value = EnumType.STRING)
  private Label label;
  @ManyToOne
  @JoinColumn(name="subject_for_survey")
  private SubjectForSurvey subjectForSurvey;

}
