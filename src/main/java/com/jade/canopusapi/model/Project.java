package com.jade.canopusapi.model;

import com.jade.canopusapi.model.utils.Goal;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Project {
    //fundamentals
    private String description;
    private Goal[] goals;
    private School school;
    private User[] stakeholders;

    //metadata
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime updatedAt;
}