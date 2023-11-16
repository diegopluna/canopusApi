package com.jade.canopusapi.models;

import com.jade.canopusapi.models.utils.Goal;

import java.time.LocalDateTime;

public class ProjectActivity {
    private Project project;
    private User[] stakeholders;
    private Goal[] goals;
    private String description;
    private LocalDateTime timestamp;
}