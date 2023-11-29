package com.jade.canopusapi.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jade.canopusapi.models.User;
import com.jade.canopusapi.models.utils.Address;
import com.jade.canopusapi.models.utils.Goal;
import com.jade.canopusapi.models.utils.UserRole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;
    @Getter
    private String fullName;
    @Getter
    private String email;
    @Getter
    private String phoneNumber;
    @Getter
    private boolean verified;
    @JsonIgnore
    private String password;

    @Getter
    private UserRole role;
    @Getter
    private Collection<Goal> interests;
    @Getter
    private Address address;
    @Getter
    private String verificationCode;
    @Getter
    private String avatar;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String fullName, String email, String phoneNumber, boolean verified, String password, UserRole role, Collection<Goal> interests, Address address, String verificationCode, String avatar, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.verified = verified;
        this.password = password;
        this.role = role;
        this.interests = interests;
        this.address = address;
        this.verificationCode = verificationCode;
        this.avatar = avatar;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().toString()));

        return new UserDetailsImpl(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getVerified(),
                user.getPassword(),
                user.getRole(),
                user.getInterests(),
                user.getAddress(),
                user.getVerificationCode(),
                user.getAvatar(),
                authorities
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return verified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}
