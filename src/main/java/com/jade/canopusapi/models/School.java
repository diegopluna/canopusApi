package com.jade.canopusapi.models;

import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.models.utils.SchoolType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "schools", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name"),
})
public class School {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=5, max = 100)
    private String name;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private SchoolType type;

    @ManyToMany
    @JoinTable(
            name = "school_users",
            joinColumns = @JoinColumn(name = "school_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> reps = new HashSet<>();

    public School(String name, Address address, SchoolType type, Set<User> reps) {
        this.name = name;
        this.address = address;
        this.type = type;
        this.reps = reps;
    }
}
