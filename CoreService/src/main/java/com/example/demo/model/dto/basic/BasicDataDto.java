package com.example.demo.model.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BasicDataDto {
    private String faculty;
    private String type;
    private String title;
    private String subject;
}
