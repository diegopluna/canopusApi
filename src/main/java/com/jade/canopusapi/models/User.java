package com.jade.canopusapi.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.models.utils.Goal;
import com.jade.canopusapi.models.utils.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phoneNumber")
})
public class User {
    //basic info
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min=5, max = 100)
    private String fullName;

    //contact info
    @NotBlank
    @Size(max = 255)
    @Email
    private String email;

    @NotBlank
    @Size(min = 11, max = 11)
    @Digits(integer = 11, fraction = 0)
    private String phoneNumber;

    //system info

    private Boolean verified;

    @NotBlank
    @Size(max = 120)
    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    //inside users only
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Collection<Goal> interests;

    @Embedded
    private Address address;

    //verification code for email verification
    @Column(name = "verification_code", length = 36)
    @JsonIgnore
    private String verificationCode;

    @Column(name = "avatar_url")
    private String avatar;

    public User(String fullName, String email, String phoneNumber, String password, Collection<Goal> interests, Address address) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.interests = interests;
        this.address = address;
    }

    public User(String fullName, String email, String phoneNumber, String password, Collection<Goal> interests, Address address, String avatar) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.interests = interests;
        this.address = address;
        this.avatar = avatar;
    }

    public User(String fullName, String email, String phoneNumber, UserRole role, Collection<Goal> interests) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.interests = interests;
    }
}
