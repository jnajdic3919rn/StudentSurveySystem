package com.example.demo.service;

import com.example.demo.model.constants.Type;
import com.example.demo.model.dto.basic.ListDto;
import com.example.demo.model.dto.basic.MessageDto;
import com.example.demo.model.dto.client.SurveyDataDto;

public interface SurveyService {

    void addNewSurvey(SurveyDataDto surveyDataDto);

    ListDto getSurveyTypes();

    ListDto getSurveys(Type type, String faculty);

    ListDto getSubjects(Type type, String faculty, String title);

    MessageDto deleteSurvey(String url, String faculty);

    MessageDto doesExist(String url, String faculty);
}
