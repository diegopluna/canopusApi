package com.jade.canopusapi.repository;

import com.jade.canopusapi.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByStakeholdersId(Long id);
}
