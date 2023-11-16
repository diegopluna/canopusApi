package com.jade.canopusapi.models;

import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.models.utils.SchoolType;

public class School {
    private String name;
    private Address address;
    private SchoolType type;
    private User[] reps;
}
