package com.example.demo.model.dto.rabbitmq;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentResultDto {
  private String surveyUrl;
  private String subjectName;
  private String comment;
  private String label;
  private String faculty;
  private String surveyType;
}
