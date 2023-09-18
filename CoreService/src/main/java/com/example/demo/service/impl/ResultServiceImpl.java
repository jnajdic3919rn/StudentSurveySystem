package com.example.demo.service.impl;

import com.example.demo.model.constants.Label;
import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Comment;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import com.example.demo.model.dto.basic.*;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.SubjectForSurveyRepository;
import com.example.demo.service.ResultService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ResultServiceImpl implements ResultService {

    private final SubjectForSurveyRepository subjectForSurveyRepository;
    private final CommentRepository commentRepository;

    @Override
    public ResultsDto getResults(BasicDataDto basicDataDto) {
        SubjectForSurvey subjectForSurvey = subjectForSurveyRepository.findBySubjectNameAndSurveyTitle(basicDataDto.getSubject(), basicDataDto.getTitle()).orElseThrow(() -> new RuntimeException());
        List<Comment> posComments = commentRepository.findComments(basicDataDto.getFaculty(), Type.valueOf(basicDataDto.getType()), basicDataDto.getTitle(), basicDataDto.getSubject(), Label.POSITIVE);
        List<Comment> negComments = commentRepository.findComments(basicDataDto.getFaculty(), Type.valueOf(basicDataDto.getType()), basicDataDto.getTitle(), basicDataDto.getSubject(), Label.NEGATIVE);

        List<HistoryData> historyData = new ArrayList<>();
        List<SubjectForSurvey> subjectForSurveys = subjectForSurveyRepository.findBySubjectNameAndSurveyType(basicDataDto.getSubject(), Type.valueOf(basicDataDto.getType()));

        historyData.add(new HistoryData("votes", subjectForSurveys.stream().map(sfs -> Integer.valueOf(sfs.getSurvey().getYear())).collect(Collectors.toList()),
                                                subjectForSurveys.stream().map(sfs -> Double.valueOf(sfs.getVotes())).collect(Collectors.toList())));
        historyData.add(new HistoryData("grades", subjectForSurveys.stream().map(sfs -> Integer.valueOf(sfs.getSurvey().getYear())).collect(Collectors.toList()),
                subjectForSurveys.stream().map(sfs -> Double.valueOf(sfs.getGrade())).collect(Collectors.toList())));

        List<BasicCommentData> basicCommentData = new ArrayList<>();
        for(SubjectForSurvey ss : subjectForSurveys){
            List<Comment> commentsP = commentRepository.findComments(ss.getSurvey().getFaculty().getShortName(), ss.getSurvey().getType(), ss.getSurvey().getTitle(), ss.getSubject().getName(), Label.POSITIVE);
            List<Comment> commentsN = commentRepository.findComments(ss.getSurvey().getFaculty().getShortName(), ss.getSurvey().getType(), ss.getSurvey().getTitle(), ss.getSubject().getName(), Label.NEGATIVE);
            basicCommentData.add(new BasicCommentData(commentsP.size(), commentsN.size(), commentsP.size()+commentsN.size(), ss.getSurvey().getYear()));
        }

        basicCommentData.sort(null);

        historyData.add(new HistoryData("comments", basicCommentData.stream().map(bcd -> bcd.getYear()).collect(Collectors.toList()),
                basicCommentData.stream().map(bcd -> Double.valueOf(bcd.getSum())).collect(Collectors.toList())));
        historyData.add(new HistoryData("pos", basicCommentData.stream().map(bcd -> bcd.getYear()).collect(Collectors.toList()),
                basicCommentData.stream().map(bcd -> Double.valueOf(bcd.getPos())).collect(Collectors.toList())));
        historyData.add(new HistoryData("neg", basicCommentData.stream().map(bcd -> bcd.getYear()).collect(Collectors.toList()),
                basicCommentData.stream().map(bcd -> Double.valueOf(bcd.getNeg())).collect(Collectors.toList())));

        return new ResultsDto(subjectForSurvey.getVotes(), subjectForSurvey.getGrade(), posComments.size() + negComments.size(), posComments.stream().map(Comment::getText).collect(Collectors.toList()),
                               negComments.stream().map(Comment::getText).collect(Collectors.toList()), historyData);
    }
}
