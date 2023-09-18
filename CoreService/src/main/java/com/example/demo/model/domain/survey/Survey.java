package com.example.demo.model.domain.survey;

import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.user.Faculty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="survey", indexes = {@Index(columnList = "type"), @Index(columnList = "type, title, faculty"), @Index(columnList = "type, year, faculty")})
@Getter
@Setter
public class Survey {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String title;
  private int year;
  private String link;
  @Enumerated(value = EnumType.STRING)
  private Type type;
  @ManyToOne
  @JoinColumn(name="faculty")
  private Faculty faculty;

}
