package com.example.demo.repository;

import com.example.demo.model.constants.Label;
import com.example.demo.model.constants.Type;
import com.example.demo.model.domain.survey.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c " +
            "WHERE (c.label = :label)  " +
            "AND (c.subjectForSurvey.subject.name LIKE :subject) " +
            "AND (c.subjectForSurvey.survey.type = :type) " +
            "AND (c.subjectForSurvey.survey.title = :title) " +
            "AND (c.subjectForSurvey.survey.faculty.shortName = :faculty)")
    List<Comment> findComments(@Param("faculty") String faculty, @Param("type") Type type, @Param("title") String title, @Param("subject") String subject, @Param("label") Label label);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.subjectForSurvey.id = :id")
    List<Comment> findCommentsBySS(@Param("id") Long id);
}
