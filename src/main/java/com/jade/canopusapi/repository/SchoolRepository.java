package com.jade.canopusapi.repository;

import com.jade.canopusapi.models.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Boolean existsByName(String name);
    School findByName(String name);

    List<School> findByRepsId(Long id);
}
