package com.jade.canopusapi.model;

import com.jade.canopusapi.model.utils.Goal;

import java.time.LocalDateTime;

public class ProjectActivity {
    private Project project;
    private User[] stakeholders;
    private Goal[] goals;
    private String description;
    private LocalDateTime timestamp;
}