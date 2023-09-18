package com.example.demo.service;

import com.example.demo.model.dto.basic.BasicDataDto;
import com.example.demo.model.dto.basic.ResultsDto;

public interface ResultService {

    ResultsDto getResults(BasicDataDto basicDataDto);
}
