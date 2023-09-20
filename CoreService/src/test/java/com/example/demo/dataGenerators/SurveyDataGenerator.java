package com.example.demo.dataGenerators;

import com.example.demo.dataGenerators.primitives.RandomDouble;
import com.example.demo.dataGenerators.primitives.RandomLong;
import com.example.demo.dataGenerators.primitives.RandomNames;
import com.example.demo.dataGenerators.primitives.RandomString;
import com.example.demo.mapper.SurveyMapper;
import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.domain.survey.Survey;
import com.example.demo.model.dto.client.SubjectDataDto;
import com.example.demo.model.dto.client.SurveyDataDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class SurveyDataGenerator {

    private final RandomString randomString;
    private final RandomNames randomNames;
    private final RandomDouble randomDouble;
    private final SubjectDataGenerator subjectDataGenerator;

    public static SurveyDataGenerator getInstance(){
        return new SurveyDataGenerator(RandomString.getInstance(), RandomNames.getInstance(), RandomDouble.getInstance(), SubjectDataGenerator.getInstance());
    }

    public SurveyDataGenerator(RandomString randomString, RandomNames randomNames, RandomDouble randomDouble, SubjectDataGenerator subjectDataGenerator) {
        this.randomString = randomString;
        this.randomNames = randomNames;
        this.randomDouble = randomDouble;
        this.subjectDataGenerator = subjectDataGenerator;
        this.generateSurveys();
        this.generateSubjectsForSurveys();
    }

    List<SurveyDataDto> surveys = new ArrayList<>();
    List<Survey> surveys2 = new ArrayList<>();
    List<SubjectForSurvey> subjectForSurveys = new ArrayList<>();
    Random random=new Random();

    public void generateSurveys(){

        for(int i = 0; i<10; i++){
            String name = randomNames.getFromRandom();
            surveys.add(new SurveyDataDto(name, Integer.valueOf(name.split(" ")[2]), subjectDataGenerator.getSubjectDataDtoList(), Type.valueOf(name.split(" ")[1]), randomString.getString(5), randomString.getString(20)));
            surveys2.add(getSurvey(surveys.get(i)));
            surveys2.get(i).setId(i+1L);
        }

    }

    public void generateSubjectsForSurveys(){

        for(int i = 0; i<50; i++){
            SubjectForSurvey subjectForSurvey = new SubjectForSurvey();
            subjectForSurvey.setSurvey(surveys2.get(Math.abs(random.nextInt())%surveys2.size()));
            subjectForSurvey.setSubject(subjectDataGenerator.getSubject());
            subjectForSurvey.setGrade(randomDouble.getDouble(5.0));
            subjectForSurvey.setVotes(Math.abs(random.nextInt())%20);
            subjectForSurvey.setId(i+1L);
            subjectForSurveys.add(subjectForSurvey);
        }
    }

    public SurveyDataDto getSurvey(){
        return surveys.get(Math.abs(random.nextInt())%surveys.size());
    }

    public Survey getSurvey2() {
        return surveys2.get(Math.abs(random.nextInt())%surveys2.size());
    }

    public List<Survey> getSurveys() {
        return surveys2;
    }

    private Survey getSurvey(SurveyDataDto surveyDataDto){
        Survey survey = new Survey();
        survey.setType(surveyDataDto.getSurveyType());
        survey.setYear(surveyDataDto.getYear());
        survey.setTitle(surveyDataDto.getSurveyTitle());
        survey.setLink(surveyDataDto.getUrl());

        return survey;
    }

    public List<SubjectForSurvey> getSubjectsForSurveys() {
        Collections.shuffle(subjectForSurveys);

        return subjectForSurveys.subList(5, 10);
    }

    public SubjectForSurvey getSubjectForSurvey(){
        return subjectForSurveys.get(Math.abs(random.nextInt())%subjectForSurveys.size());
    }

    public List<SubjectForSurvey> generateAndGet(List<Subject> subjects, Survey survey){
        List<SubjectForSurvey> filtered = new ArrayList<>();
        for(int i = 0; i<subjects.size(); i++){
            SubjectForSurvey subjectForSurvey = new SubjectForSurvey();
            subjectForSurvey.setSurvey(survey);
            subjectForSurvey.setSubject(subjects.get(i));
            subjectForSurvey.setGrade(randomDouble.getDouble(5.0));
            subjectForSurvey.setVotes(Math.abs(random.nextInt())%20);
            subjectForSurvey.setId(i+1L);
            filtered.add(subjectForSurvey);
        }
        return filtered;
    }

}

