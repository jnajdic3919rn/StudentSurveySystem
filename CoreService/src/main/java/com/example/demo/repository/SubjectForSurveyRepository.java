package com.example.demo.repository;

import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.SubjectForSurvey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectForSurveyRepository extends JpaRepository<SubjectForSurvey, Long> {

    @Query("SELECT ss FROM SubjectForSurvey ss " +
            "WHERE ss.subject.name LIKE :subject AND ss.survey.link LIKE :survey")
    Optional<SubjectForSurvey> findBySubjectNameAndSurveyUrl(@Param("subject") String subjectName, @Param("survey") String surveyUrl);

    @Query("SELECT ss FROM SubjectForSurvey ss " +
            "WHERE ss.subject.name LIKE :subject AND ss.survey.title LIKE :survey")
    Optional<SubjectForSurvey> findBySubjectNameAndSurveyTitle(@Param("subject") String subject, @Param("survey") String title);

    @Query("SELECT ss FROM SubjectForSurvey ss " +
            "WHERE (ss.survey.title = :title) " +
            "AND (ss.survey.type = :type) " +
            "AND ss.survey.faculty.shortName LIKE :faculty")
    List<SubjectForSurvey> findByTitleAndTypeAndFaculty(@Param("title") String title, @Param("type") Type type, @Param("faculty") String faculty);


    @Query("SELECT ss FROM SubjectForSurvey ss " +
            "WHERE (ss.survey.link = :url)")
    List<SubjectForSurvey> findSubjectBySurveyLink(@Param("url") String url);

    @Query("SELECT ss FROM SubjectForSurvey ss " +
            "WHERE (ss.subject.name LIKE :subject) " +
            "AND (ss.survey.type = :type) " +
            "ORDER BY ss.survey.year ASC")
    List<SubjectForSurvey> findBySubjectNameAndSurveyType(@Param("subject") String subject, @Param("type") Type type);
}
