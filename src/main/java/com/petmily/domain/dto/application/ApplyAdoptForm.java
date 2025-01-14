package com.petmily.domain.dto.application;

import com.petmily.domain.enum_type.LocationType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ApplyAdoptForm {

    @NotNull
    private LocationType location;

    @NotBlank
    private String job;

    @NotNull
    private Boolean married;
}
