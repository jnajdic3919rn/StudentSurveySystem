package com.example.demo.model.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BasicCommentData implements Comparable<BasicCommentData>{
    private int pos;
    private int neg;
    private int sum;
    private int year;

    @Override
    public int compareTo(BasicCommentData o) {
        return this.year-o.getYear();
    }
}
