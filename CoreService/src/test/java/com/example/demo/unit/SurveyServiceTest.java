package com.example.demo.unit;

import com.example.demo.dataGenerators.CommentDataGenerator;
import com.example.demo.dataGenerators.FacultyGenerator;
import com.example.demo.dataGenerators.SubjectDataGenerator;
import com.example.demo.dataGenerators.SurveyDataGenerator;
import com.example.demo.dataGenerators.primitives.RandomNames;
import com.example.demo.mapper.SubjectMapper;
import com.example.demo.mapper.SurveyMapper;
import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.domain.survey.Survey;
import com.example.demo.model.domain.user.Faculty;
import com.example.demo.model.dto.basic.*;
import com.example.demo.model.dto.client.SurveyDataDto;
import com.example.demo.repository.*;
import com.example.demo.service.ResultService;
import com.example.demo.service.SurveyService;
import com.example.demo.service.impl.ResultServiceImpl;
import com.example.demo.service.impl.SurveyServiceImpl;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

public class SurveyServiceTest {

    private SubjectDataGenerator subjectDataGenerator = SubjectDataGenerator.getInstance();
    private SurveyDataGenerator surveyDataGenerator = SurveyDataGenerator.getInstance();
    private FacultyGenerator facultyGenerator = FacultyGenerator.getInstance();
    private CommentDataGenerator commentDataGenerator = CommentDataGenerator.getInstance();

    private RandomNames randomNames = RandomNames.getInstance();

    private SurveyRepository surveyRepository;
    private SubjectRepository subjectRepository;
    private CommentRepository commentRepository;
    private FacultyRepository facultyRepository;
    private SubjectForSurveyRepository subjectForSurveyRepository;
    private SurveyMapper surveyMapper;
    private SubjectMapper subjectMapper;

    private SurveyService surveyService;
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

        this.surveyService = new SurveyServiceImpl(surveyRepository, subjectRepository, commentRepository, subjectForSurveyRepository, surveyMapper, subjectMapper);
        this.resultService = new ResultServiceImpl(subjectForSurveyRepository, commentRepository);
    }

    @Test
    public void addNewSurvey() {
        SurveyDataDto surveyDataDto = surveyDataGenerator.getSurvey();
        Faculty faculty = facultyGenerator.getFaculty();

        long surveyId = 5;
        Survey survey = new Survey();
        survey.setLink(surveyDataDto.getUrl());
        survey.setFaculty(faculty);
        survey.setTitle(surveyDataDto.getSurveyTitle());
        survey.setType(surveyDataDto.getSurveyType());
        survey.setYear(surveyDataDto.getYear());

        given(surveyMapper.toSurvey(surveyDataDto)).willReturn(survey);
        survey.setId(surveyId);

        given(surveyRepository.save(any())).willReturn(survey);


        for(int i = 0; i<surveyDataDto.getSubjectData().size(); i++) {
            Subject subject = new Subject();
            subject.setId(i+1L);
            subject.setName(surveyDataDto.getSubjectData().get(i).getSubjectName());
            given(subjectRepository.save(any())).willReturn(subject);
        }

        surveyService.addNewSurvey(surveyDataDto);

        subjectForSurveyRepository.findSubjectBySurveyLink(survey.getLink());

        verify(subjectRepository, times(surveyDataDto.getSubjectData().size())).save(any());
        Assertions.assertEquals(surveyId, survey.getId());
        Assertions.assertEquals(surveyDataDto.getUrl(), survey.getLink());
        Assertions.assertEquals(faculty, survey.getFaculty());
        Assertions.assertEquals(surveyDataDto.getSurveyTitle(), survey.getTitle());
        Assertions.assertEquals(surveyDataDto.getSurveyType(), survey.getType());
        Assertions.assertEquals(surveyDataDto.getYear(), survey.getYear());

    }

    @Test
    public void testGetSurveyTypes() {
        List<Type> surveyTypeList = Arrays.asList(Type.values());
        List<String> expectedTypes = surveyTypeList.stream()
                .map(Enum::toString)
                .collect(Collectors.toList());

        ListDto result = surveyService.getSurveyTypes();

        Assertions.assertEquals(expectedTypes, result.getList());
    }

    @Test
    public void testGetSurveys() {
        Random random = new Random();
        Type type = Type.values()[Math.abs(random.nextInt())%Type.values().length];
        String faculty = "RAF";

        List<Survey> mockSurveys = surveyDataGenerator.getSurveys();

        when(surveyRepository.findByTypeAndFaculty(type, faculty)).thenReturn(mockSurveys);

        List<String> expectedSurveyTitles = mockSurveys.stream()
                .map(Survey::getTitle)
                .collect(Collectors.toList());

        ListDto result = surveyService.getSurveys(type, faculty);

        Assertions.assertEquals(expectedSurveyTitles, result.getList());
    }

    @Test
    public void testGetSubjects() {
        Random random = new Random();
        Type type = Type.values()[Math.abs(random.nextInt())%Type.values().length];
        String faculty = "RAF";
        String title = RandomNames.getInstance().getFromRandom();

        List<SubjectForSurvey> subjectForSurveys = surveyDataGenerator.getSubjectsForSurveys();

        when(subjectForSurveyRepository.findByTitleAndTypeAndFaculty(title, type, faculty)).thenReturn(subjectForSurveys);

        List<String> expectedSubjectNames = subjectForSurveys.stream()
                .map(subjectForSurvey -> subjectForSurvey.getSubject().getName())
                .collect(Collectors.toList());

        ListDto result = surveyService.getSubjects(type, faculty, title);

        Assertions.assertEquals(expectedSubjectNames, result.getList());
    }

    @Test
    public void testDeleteSurveySuccessfulDeletion() {

        Survey survey = surveyDataGenerator.getSurvey2();
        Faculty faculty = facultyGenerator.getFaculty();
        survey.setFaculty(faculty);

        List<Subject> subjects = new ArrayList<>();
        subjects.add(subjectDataGenerator.getSubject());
        subjects.add(subjectDataGenerator.getSubject());

        when(surveyRepository.findByLinkAndFaculty(survey.getLink(), faculty.getShortName()))
                .thenReturn(Optional.of(survey));

        List<SubjectForSurvey> mockSubjectForSurveys = surveyDataGenerator.generateAndGet(subjects, survey);

        when(subjectForSurveyRepository.findSubjectBySurveyLink(survey.getLink()))
                .thenReturn(mockSubjectForSurveys);

        List<Comment> mockComments = new ArrayList<>();
        mockComments.addAll(commentDataGenerator.generateAndGetComments(survey, subjects.get(0)));
        mockComments.addAll(commentDataGenerator.generateAndGetComments(survey, subjects.get(1)));

        when(commentRepository.findCommentsBySS(anyLong())).thenReturn(mockComments);

        MessageDto result = surveyService.deleteSurvey(survey.getLink(), faculty.getShortName());

        Assertions.assertEquals("Anketa sa url-om: " + survey.getLink() + " je uspešno obrisana", result.getMessage());
        Assertions.assertTrue(result.isFlag());

        verify(commentRepository, times(subjects.size())).deleteAll(mockComments);
        verify(subjectForSurveyRepository, times(1)).deleteAll(mockSubjectForSurveys);
        verify(surveyRepository, times(1)).delete(survey);
    }

    @Test
    public void testDoesExistSurveyExists() {
        String url = "example.com/survey";
        String faculty = "RAF";
        String surveyTitle = "Sample Survey";

        Survey survey = new Survey();
        survey.setTitle(surveyTitle);
        Mockito.when(surveyRepository.findByLinkAndFaculty(url, faculty)).thenReturn(Optional.of(survey));

        MessageDto result = surveyService.doesExist(url, faculty);

        Assertions.assertTrue(result.isFlag());
        Assertions.assertEquals("Anketa sa datim url-om je već preuzeta pod nazivom " + surveyTitle, result.getMessage());
    }

    @Test
    public void testDoesExistSurveyDoesNotExist() {
        String url = "example.com/survey";
        String faculty = "RAF";

        Mockito.when(surveyRepository.findByLinkAndFaculty(url, faculty)).thenReturn(Optional.empty());

        MessageDto result = surveyService.doesExist(url, faculty);

        Assertions.assertFalse(result.isFlag());
        Assertions.assertEquals("Anketa sa datim url-om nije preuzeta", result.getMessage());
    }


}
