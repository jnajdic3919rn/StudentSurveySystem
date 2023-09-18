package com.example.demo.model.domain.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name="faculty", indexes = {@Index(columnList = "name"), @Index(columnList = "university, priv")})
@Getter
@Setter
public class Faculty {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private String shortName;
  private String city;
  private boolean priv;
  private String university;

}
