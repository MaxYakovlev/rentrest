package ru.yakovlev.rentrest.model.dto.rent;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
public class CreateRentDto {
    @Positive
    @NotNull
    private Long transportId;
}
