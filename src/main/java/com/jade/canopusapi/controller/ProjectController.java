package com.jade.canopusapi.controller;


import com.jade.canopusapi.dao.ProjectDAO;
import com.jade.canopusapi.dao.SchoolDAO;
import com.jade.canopusapi.dao.UserDAO;
import com.jade.canopusapi.models.Project;
import com.jade.canopusapi.models.School;
import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.UserRole;
import com.jade.canopusapi.payload.request.CreateProjectRequest;
import com.jade.canopusapi.payload.response.MessageResponse;
import com.jade.canopusapi.payload.response.ProjectResponse;
import com.jade.canopusapi.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private SchoolDAO schoolDAO;

    @Autowired
    private ProjectDAO projectDAO;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectRequest request) {
        School school = schoolDAO.findSchoolById(request.getSchoolId());
        if (school == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Escola não existe"));
        }
        Collection<Long> userIds = request.getUserIds();
        if (userIds != null && !userIds.isEmpty()) {
            Collection<User> stakeholders = new HashSet<>();
            for(Long id: userIds) {
                User user = userDAO.findUserById(id);
                if (user == null) {
                    return ResponseEntity.badRequest().body(new MessageResponse("Usuário de id: "+id+" não existe"));
                }
                stakeholders.add(user);
            }
            Project proj = new Project(request.getDescription(), request.getGoals(), school, stakeholders, request.getStartDate(), request.getEndDate());
            return projectDAO.createProject(proj);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Lista de usuários está vazia"));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<?> getAll() {
        List<Project> projs = projectDAO.getAllProjects();
        if (!projs.isEmpty()) {
            return ResponseEntity.ok(projs);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/stakeholder")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR') or hasAuthority('REP_ESCOLA') or hasAuthority('VOLUNTARIO')")
    public ResponseEntity<?> getUserProjects() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = userDetails.getId();
        List<Project> projs = projectDAO.getProjectsByUserId(id);
        if (!projs.isEmpty()) {
            return ResponseEntity.ok(projs);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMBAIXADOR') or hasAuthority('ADMINISTRADOR') or hasAuthority('REP_ESCOLA') or hasAuthority('VOLUNTARIO')")
    public ResponseEntity<?> getProjectSummary(@PathVariable Long id) {
        Project proj = projectDAO.getProjectById(id);
        if (proj == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Projeto não existe"));
        }
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectResponse response = new ProjectResponse(proj.getDescription(), proj.getGoals(), proj.getSchool().getName(), proj.getStartDate(), proj.getEndDate(), proj.getUpdatedAt());
        return  ResponseEntity.ok().body(response);

    }

}
