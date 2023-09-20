package com.example.demo.unit;

import com.example.demo.dataGenerators.CommentDataGenerator;
import com.example.demo.dataGenerators.FacultyGenerator;
import com.example.demo.dataGenerators.SubjectDataGenerator;
import com.example.demo.dataGenerators.SurveyDataGenerator;
import com.example.demo.mapper.SubjectMapper;
import com.example.demo.mapper.SurveyMapper;
import com.example.demo.model.constants.Label;
import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.domain.survey.Survey;
import com.example.demo.model.dto.basic.BasicDataDto;
import com.example.demo.model.dto.basic.HistoryData;
import com.example.demo.model.dto.basic.ResultsDto;
import com.example.demo.repository.*;
import com.example.demo.service.ResultService;
import com.example.demo.service.impl.ResultServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

public class ResultServiceTest {
    private SubjectDataGenerator subjectDataGenerator = SubjectDataGenerator.getInstance();
    private SurveyDataGenerator surveyDataGenerator = SurveyDataGenerator.getInstance();
    private FacultyGenerator facultyGenerator = FacultyGenerator.getInstance();
    private CommentDataGenerator commentDataGenerator = CommentDataGenerator.getInstance();

    private SurveyRepository surveyRepository;
    private SubjectRepository subjectRepository;
    private CommentRepository commentRepository;
    private FacultyRepository facultyRepository;
    private SubjectForSurveyRepository subjectForSurveyRepository;
    private SurveyMapper surveyMapper;
    private SubjectMapper subjectMapper;

    private ResultService resultService;

    @BeforeEach
    public void prepare(){
        this.facultyRepository=mock(FacultyRepository.class);
        this.surveyRepository=mock(SurveyRepository.class);
        this.subjectRepository=mock(SubjectRepository.class);
        this.surveyMapper=mock(SurveyMapper.class);
        this.subjectMapper=mock(SubjectMapper.class);
        this.commentRepository = mock(CommentRepository.class);
        this.subjectForSurveyRepository = mock(SubjectForSurveyRepository.class);

        this.resultService = new ResultServiceImpl(subjectForSurveyRepository, commentRepository);
    }

    @Test
    public void testGetResults() {
        BasicDataDto basicDataDto = new BasicDataDto("RAF", "NASTAVA_NEPARNI", "Anketa raf", "Subject");

        Subject subject = subjectDataGenerator.getSubject();
        subject.setName(basicDataDto.getSubject());
        Subject subject2 = subjectDataGenerator.getSubject();
        subject2.setName(basicDataDto.getSubject());
        Survey survey = surveyDataGenerator.getSurvey2();
        survey.setTitle(basicDataDto.getTitle());
        survey.setType(Type.valueOf(basicDataDto.getType()));

        Survey survey2 = surveyDataGenerator.getSurvey2();
        survey2.setTitle(basicDataDto.getTitle());
        survey2.setType(Type.valueOf(basicDataDto.getType()));

        List<SubjectForSurvey> subjectForSurvey = surveyDataGenerator.generateAndGet(List.of(subject, subject2), survey);

        List<Comment> posComments = commentDataGenerator.getCommentList()
                .stream()
                .filter(comment -> comment.getLabel() == Label.POSITIVE)
                .collect(Collectors.toList());
        List<Comment> negComments = commentDataGenerator.getCommentList()
                .stream()
                .filter(comment -> comment.getLabel() == Label.NEGATIVE)
                .collect(Collectors.toList());

        List<HistoryData> historyData = new ArrayList<>();
        List<Integer> years = List.of(survey.getYear(), survey2.getYear());

        historyData.add(new HistoryData("votes", years, List.of(Double.valueOf(subjectForSurvey.get(0).getVotes()),Double.valueOf(subjectForSurvey.get(1).getVotes()))));
        historyData.add(new HistoryData("grades", years, List.of(Double.valueOf(subjectForSurvey.get(0).getGrade()),Double.valueOf(subjectForSurvey.get(1).getGrade()))));

        Mockito.when(subjectForSurveyRepository.findBySubjectNameAndSurveyTitle(basicDataDto.getSubject(), basicDataDto.getTitle()))
                .thenReturn(java.util.Optional.ofNullable(subjectForSurvey.get(0)));
        Mockito.when(commentRepository.findComments(basicDataDto.getFaculty(), Type.valueOf(basicDataDto.getType()), basicDataDto.getTitle(), basicDataDto.getSubject(), Label.POSITIVE))
                .thenReturn(posComments);
        Mockito.when(commentRepository.findComments(basicDataDto.getFaculty(), Type.valueOf(basicDataDto.getType()), basicDataDto.getTitle(), basicDataDto.getSubject(), Label.NEGATIVE))
                .thenReturn(negComments);

        // Call the getResults method
        ResultsDto resultsDto = resultService.getResults(basicDataDto);

        // Assertions
        Assertions.assertEquals(subjectForSurvey.get(0).getVotes(), resultsDto.getVotes());
        Assertions.assertEquals(subjectForSurvey.get(0).getGrade(), resultsDto.getGrade());
        Assertions.assertEquals(50, resultsDto.getNegativeComments().size()+resultsDto.getPositiveComments().size());
        Assertions.assertFalse(resultsDto.getPositiveComments().isEmpty());
        Assertions.assertFalse(resultsDto.getPositiveComments().isEmpty());
        Assertions.assertFalse(resultsDto.getHistoryData().isEmpty());
    }
}
