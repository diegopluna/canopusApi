package com.jade.canopusapi.model;

import com.jade.canopusapi.model.utils.Address;
import com.jade.canopusapi.model.utils.SchoolType;

public class School {
    private String name;
    private Address address;
    private SchoolType type;
    private User[] reps;
}
