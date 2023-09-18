package com.example.demo.repository;

import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @Query("SELECT s FROM Survey s " +
            "WHERE (s.type = :type) " +
            "AND s.faculty.shortName LIKE :faculty")
    List<Survey> findByTypeAndFaculty(@Param("type")Type type, @Param("faculty") String shortName);

    Optional<Survey> findByLink(String url);

    @Query("SELECT s FROM Survey s " +
            "WHERE (s.link LIKE :url) " +
            "AND (:faculty IS NULL OR s.faculty.shortName LIKE :faculty)")
    Optional<Survey> findByLinkAndFaculty(@Param("url") String url, @Param("faculty") String shortName);
}
