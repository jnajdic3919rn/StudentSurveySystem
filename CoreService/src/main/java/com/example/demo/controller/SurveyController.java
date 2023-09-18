package com.example.demo.controller;

import com.example.demo.model.constants.Type;
import com.example.demo.model.dto.basic.ListDto;
import com.example.demo.model.dto.client.SurveyDataDto;
import com.example.demo.service.SurveyService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/data")
@AllArgsConstructor
public class SurveyController {

  private final SurveyService surveyService;

  @PostMapping("/survey_info")
  public ResponseEntity<String> saveSurveyData(@RequestBody SurveyDataDto surveyDataDto){
    surveyService.addNewSurvey(surveyDataDto);
    return new ResponseEntity<>(new String("Uspeh"), HttpStatus.OK);
  }

  @GetMapping("/survey/types")
  public ResponseEntity<ListDto> getSurveyTypes(){
    return new ResponseEntity<>(surveyService.getSurveyTypes(), HttpStatus.OK);
  }

  @GetMapping("/survey")
  public ResponseEntity<ListDto> getSurveys(@RequestParam Type type, @RequestParam String faculty){
    return new ResponseEntity<>(surveyService.getSurveys(type, faculty), HttpStatus.OK);
  }

  @GetMapping("/subject")
  public ResponseEntity<ListDto> getSubjects(@RequestParam Type type, @RequestParam String faculty, @RequestParam String title){
    return new ResponseEntity<>(surveyService.getSubjects(type, faculty, title), HttpStatus.OK);
  }

  @DeleteMapping("/survey")
  public ResponseEntity<String> deleteSurvey(@RequestParam String url){
    return new ResponseEntity<>(surveyService.deleteSurvey(url), HttpStatus.OK);
  }
}
