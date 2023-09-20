package com.example.demo.service.impl;

import com.example.demo.mapper.SubjectMapper;
import com.example.demo.mapper.SurveyMapper;
import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.domain.survey.Survey;
import com.example.demo.model.dto.basic.ListDto;
import com.example.demo.model.dto.basic.MessageDto;
import com.example.demo.model.dto.client.SubjectDataDto;
import com.example.demo.model.dto.client.SurveyDataDto;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.SubjectForSurveyRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.repository.SurveyRepository;
import com.example.demo.service.SurveyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;
    private final SubjectRepository subjectRepository;
    private final CommentRepository commentRepository;
    private final SubjectForSurveyRepository subjectForSurveyRepository;

    private final SurveyMapper surveyMapper;
    private final SubjectMapper subjectMapper;

    @Override
    public void addNewSurvey(SurveyDataDto surveyDataDto) {
        Survey s = surveyRepository.findByLinkAndFaculty(surveyDataDto.getUrl(), surveyDataDto.getFaculty()).orElse(null);
        Survey survey;
        if(s == null) {
            survey = surveyMapper.toSurvey(surveyDataDto);

            survey = surveyRepository.save(survey);
        }
        else
            survey = s;

        for(SubjectDataDto subjectDto : surveyDataDto.getSubjectData()) {
            Subject subject = subjectRepository.findByName(subjectDto.getSubjectName()).orElse(null);
            if(subject == null) {
                subject = subjectMapper.toSubject(subjectDto.getSubjectName());
                subject = subjectRepository.save(subject);
            }
            SubjectForSurvey subjectForSurvey = subjectMapper.toSubjectForSurvey(survey, subject, subjectDto);
            subjectForSurveyRepository.save(subjectForSurvey);
        }
    }

    @Override
    public ListDto getSurveyTypes() {
        List<String> types = Arrays.stream(Type.values())
                .map(Enum::toString)
                .collect(Collectors.toList());

        return new ListDto(types);
    }

    @Override
    public ListDto getSurveys(Type type, String faculty) {
        List<Survey> surveys = surveyRepository.findByTypeAndFaculty(type, faculty);
        return new ListDto(surveys.stream()
                .map(Survey::getTitle) // Extract the name field from each Survey object
                .collect(Collectors.toList()));
    }

    @Override
    public ListDto getSubjects(Type type, String faculty, String title) {

        return new ListDto(subjectForSurveyRepository.findByTitleAndTypeAndFaculty(title, type, faculty).
                stream().map(SubjectForSurvey::getSubject)
                .map(Subject::getName) // Extract the name field from each Subject object
                .collect(Collectors.toList()));
    }

    @Override
    public MessageDto deleteSurvey(String url, String faculty) {
        Survey survey = surveyRepository.findByLinkAndFaculty(url, faculty).orElse(null);

        if(survey == null) return new MessageDto("Anketa sa url-om: " + url + " nije pronađena", false);
        List<SubjectForSurvey> subjectForSurveys = subjectForSurveyRepository.findSubjectBySurveyLink(url);
        for(SubjectForSurvey subject : subjectForSurveys){
            List<Comment> comments = commentRepository.findCommentsBySS(subject.getId());
            commentRepository.deleteAll(comments);
        }

        subjectForSurveyRepository.deleteAll(subjectForSurveys);
        surveyRepository.delete(survey);

        return new MessageDto("Anketa sa url-om: " + url + " je uspešno obrisana", true);
    }

    @Override
    public MessageDto doesExist(String url, String faculty) {
        Survey survey = surveyRepository.findByLinkAndFaculty(url, faculty).orElse(null);
        if(survey != null) return new MessageDto("Anketa sa datim url-om je već preuzeta pod nazivom " + survey.getTitle(), true);

        return new MessageDto("Anketa sa datim url-om nije preuzeta", false);
    }
}
