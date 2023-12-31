package com.example.demo.model.domain.survey;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="subject", indexes = {@Index(columnList = "name")})
@Getter
@Setter
public class Subject {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
}
