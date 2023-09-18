package com.example.demo.mapper;

import com.example.demo.model.domain.survey.Survey;
import com.example.demo.model.dto.client.SurveyDataDto;
import com.example.demo.repository.FacultyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SurveyMapper {

  private final FacultyRepository facultyRepository;

  public Survey toSurvey(SurveyDataDto surveyDataDto){
    Survey survey = new Survey();
    survey.setTitle(surveyDataDto.getSurveyTitle());
    survey.setYear(surveyDataDto.getYear());
    survey.setLink(surveyDataDto.getUrl());
    survey.setType(surveyDataDto.getSurveyType());
    survey.setFaculty(facultyRepository.findById(1L).get());

    return survey;
  }
}
