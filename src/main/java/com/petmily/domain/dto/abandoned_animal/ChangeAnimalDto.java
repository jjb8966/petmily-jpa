package com.petmily.domain.dto.abandoned_animal;

import com.petmily.domain.enum_type.AnimalSpecies;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeAnimalDto {

    private AnimalSpecies species;
    private String name;
    private String kind;
    private Integer age;
    private Float weight;
}
