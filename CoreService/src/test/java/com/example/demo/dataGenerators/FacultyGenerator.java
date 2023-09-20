package com.example.demo.dataGenerators;

import com.example.demo.dataGenerators.primitives.RandomDouble;
import com.example.demo.dataGenerators.primitives.RandomString;
import com.example.demo.model.domain.user.Faculty;

import org.springframework.stereotype.Component;

@Component
public class FacultyGenerator {

    private final RandomString randomString;

    public FacultyGenerator(RandomString randomString) {
        this.randomString = randomString;
    }

    public static FacultyGenerator getInstance(){
        return new FacultyGenerator(RandomString.getInstance());
    }

    public Faculty getFaculty(){
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setCity(randomString.getString(10));
        faculty.setName(randomString.getString(10));
        faculty.setShortName("RAF");
        faculty.setPriv(true);
        faculty.setUniversity(randomString.getString(5));

        return faculty;
    }
}
