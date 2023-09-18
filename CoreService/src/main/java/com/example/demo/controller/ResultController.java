package com.example.demo.controller;

import com.example.demo.model.dto.basic.BasicDataDto;
import com.example.demo.model.dto.basic.ResultsDto;
import com.example.demo.service.ResultService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/results")
@AllArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @PostMapping
    public ResponseEntity<ResultsDto> getResults(@RequestBody BasicDataDto basicDataDto){
        return new ResponseEntity<>(resultService.getResults(basicDataDto), HttpStatus.OK);
    }


}
