package com.jade.canopusapi.dao;


import com.jade.canopusapi.models.Project;
import com.jade.canopusapi.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectDAO {

    @Autowired
    ProjectRepository projectRepository;


    public ResponseEntity<?> createProject(Project project) {
        projectRepository.save(project);
        return ResponseEntity.status(201).body("Projeto criado");
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByUserId(Long id) {
        return projectRepository.findByStakeholdersId(id);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }
}
