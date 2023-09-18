package com.example.demo.model.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResultsDto {
    private int votes;
    private double grade;
    private int numberOfComments;
    private List<String> positiveComments;
    private List<String> negativeComments;
    private List<HistoryData> historyData;
}
