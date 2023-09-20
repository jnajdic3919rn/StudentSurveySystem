package com.example.demo.dataGenerators;

import com.example.demo.dataGenerators.primitives.RandomDouble;
import com.example.demo.dataGenerators.primitives.RandomNames;
import com.example.demo.dataGenerators.primitives.RandomString;
import com.example.demo.model.constants.Label;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.domain.survey.Survey;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class CommentDataGenerator {
    private final RandomString randomString;
    private final RandomNames randomNames;
    private final RandomDouble randomDouble;
    private final SurveyDataGenerator surveyDataGenerator;

    Random random = new Random();

    public static CommentDataGenerator getInstance(){
        return new CommentDataGenerator(RandomString.getInstance(), RandomNames.getInstance(), RandomDouble.getInstance(), SurveyDataGenerator.getInstance());
    }

    public CommentDataGenerator(RandomString randomString, RandomNames randomNames, RandomDouble randomDouble, SurveyDataGenerator surveyDataGenerator) {
        this.randomString = randomString;
        this.randomNames = randomNames;
        this.randomDouble = randomDouble;
        this.surveyDataGenerator = surveyDataGenerator;
        this.generateComments();
    }

    private List<Comment> commentList = new ArrayList<>();

    public void generateComments(){
        for(int i = 0; i<50; i++){
            Comment comment = new Comment();
            comment.setId(i+1L);
            if(i%2 == 0)
                comment.setLabel(Label.NEGATIVE);
            else
                comment.setLabel(Label.POSITIVE);
            comment.setText(this.randomString.getString(10));
            comment.setSubjectForSurvey(this.surveyDataGenerator.getSubjectForSurvey());
            commentList.add(comment);
        }
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public List<Comment> getCommentList(Survey survey) {
        List<Comment> comments = new ArrayList<>();
        for(Comment comment : commentList){
            if(comment.getSubjectForSurvey().getSurvey().getLink().equals(survey.getLink()) && comment.getSubjectForSurvey().getSurvey().getFaculty().getShortName().equals(survey.getFaculty()))
                comments.add(comment);
        }
        return comments;
    }

    public List<Comment> generateAndGetComments(Survey survey, Subject subject){
        List<Comment> commentsFiler = new ArrayList<>();
        SubjectForSurvey subjectForSurvey = new SubjectForSurvey();
        subjectForSurvey.setSubject(subject);
        subjectForSurvey.setSurvey(survey);
        subjectForSurvey.setVotes(Math.abs(random.nextInt(20)));
        subjectForSurvey.setGrade(randomDouble.getDouble(5.0));
        subjectForSurvey.setId(1L);

        for(int i = 0; i<10; i++){
            Comment comment = new Comment();
            comment.setId(i+1L);
            if(i%2 == 0)
                comment.setLabel(Label.NEGATIVE);
            else
                comment.setLabel(Label.POSITIVE);
            comment.setText(this.randomString.getString(10));
            comment.setSubjectForSurvey(subjectForSurvey);
            commentsFiler.add(comment);
        }

        return commentsFiler;
    }


}
