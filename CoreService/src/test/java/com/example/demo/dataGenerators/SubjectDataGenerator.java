package com.example.demo.dataGenerators;

import com.example.demo.dataGenerators.primitives.RandomDouble;
import com.example.demo.dataGenerators.primitives.RandomString;
import com.example.demo.model.domain.survey.Subject;
import com.example.demo.model.dto.client.SubjectDataDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class SubjectDataGenerator {

    private final RandomString randomString;
    private final RandomDouble randomDouble;

    public static SubjectDataGenerator getInstance(){
        return new SubjectDataGenerator(RandomString.getInstance(),RandomDouble.getInstance());
    }

    public SubjectDataGenerator(RandomString randomString, RandomDouble randomDouble) {
        this.randomString = randomString;
        this.randomDouble = randomDouble;
        generate();
    }

    private List<SubjectDataDto> subjectDataDtoList = new ArrayList<>();
    private List<Subject> subjectList = new ArrayList<>();
    Random random=new Random();

    public void generate(){
        for(int i = 0; i<30; i++){
            subjectDataDtoList.add(new SubjectDataDto(randomString.getString(10), 10 + random.nextInt()%20, randomDouble.getDouble(5.0)));
            Subject subject = new Subject();
            subject.setName(subjectDataDtoList.get(i).getSubjectName());
            subject.setId(i+1L);
            subjectList.add(subject);
        }
    }

    public List<SubjectDataDto> getSubjectDataDtoList() {
        Collections.shuffle(subjectDataDtoList);

        return subjectDataDtoList.subList(5, 10);
    }

    public Subject getSubject(){
        return subjectList.get(Math.abs(random.nextInt())%subjectList.size());
    }
}
