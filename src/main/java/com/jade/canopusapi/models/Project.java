package com.jade.canopusapi.models;

import com.jade.canopusapi.models.utils.Goal;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //fundamentals

    @NotBlank
    @Size(min=10, max = 1000)
    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Collection<Goal> goals;

    @ManyToOne
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToMany
    @JoinTable(
            name = "project_stakeholders", // Assuming a joining table for the project-stakeholder relationship
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Collection<User> stakeholders;

    //metadata
    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime updatedAt;

    public Project(String description, Collection<Goal> goals, School school, Collection<User> stakeholders, LocalDate startDate, LocalDate endDate) {
        this.description = description;
        this.goals = goals;
        this.school = school;
        this.stakeholders = stakeholders;
        this.startDate = startDate;
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }
}