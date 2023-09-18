package com.example.demo.model.dto.client;

import com.example.demo.model.constants.Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SurveyDataDto {
  private String surveyTitle;
  private int year;
  private List<SubjectDataDto> subjectData;
  private Type surveyType;
  private String faculty;
  private String url;

}
