package com.jade.canopusapi.payload.request;

import com.jade.canopusapi.models.utils.Goal;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Collection;


@Getter
@Setter
@NoArgsConstructor
public class CreateProjectRequest {

    @NotBlank
    @Size(min=10, max = 1000)
    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Collection<Goal> goals;

    private Long schoolId;

    private Collection<Long> userIds;

    private LocalDate startDate;
    private LocalDate endDate;

}
