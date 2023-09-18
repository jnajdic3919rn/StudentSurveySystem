package com.example.demo.repository;

import com.example.demo.model.domain.survey.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

  Optional<Subject> findByName(String subjectName);
}
