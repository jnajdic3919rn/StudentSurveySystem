package com.example.demo.mapper;

import com.example.demo.model.constants.Label;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.dto.rabbitmq.CommentResultDto;
import com.example.demo.repository.SubjectForSurveyRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CommentMapper {

  private final SubjectForSurveyRepository subjectForSurveyRepository;

  public Comment toComment(CommentResultDto commentResultDto){
    SubjectForSurvey subjectForSurvey = subjectForSurveyRepository.findBySubjectNameAndSurveyUrl(commentResultDto.getSubjectName(), commentResultDto.getSurveyUrl()).orElseThrow(()->new RuntimeException());
    Comment comment = new Comment();
    comment.setText(commentResultDto.getComment());
    comment.setLabel(Label.valueOf(commentResultDto.getLabel().toUpperCase()));

    comment.setSubjectForSurvey(subjectForSurvey);

    return comment;
  }

}
