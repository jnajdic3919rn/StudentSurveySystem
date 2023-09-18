package com.example.demo.mapper;

import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.domain.survey.Survey;
import com.example.demo.model.dto.client.SubjectDataDto;
import org.springframework.stereotype.Component;

@Component
public class SubjectMapper {

  public Subject toSubject(String subjectName){
    Subject subject = new Subject();
    subject.setName(subjectName);

    return subject;
  }

  public SubjectForSurvey toSubjectForSurvey(Survey survey, Subject subject, SubjectDataDto subjectDataDto){
    SubjectForSurvey subjectForSurvey = new SubjectForSurvey();
    subjectForSurvey.setSurvey(survey);
    subjectForSurvey.setSubject(subject);
    subjectForSurvey.setVotes(subjectDataDto.getVotes());
    subjectForSurvey.setGrade(subjectDataDto.getGrade());
    subjectForSurvey.setSurvey(survey);

    return subjectForSurvey;
  }


}
