package com.jade.canopusapi.payload.response;

import com.jade.canopusapi.models.School;
import com.jade.canopusapi.models.utils.Goal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;

@AllArgsConstructor
public class ProjectResponse {
    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Collection<Goal> goals;

    private String schoolName;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDateTime updatedAt;

}
