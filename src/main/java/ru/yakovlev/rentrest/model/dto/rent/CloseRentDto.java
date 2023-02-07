package ru.yakovlev.rentrest.model.dto.rent;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class CloseRentDto {
    @Positive
    private double endRentLatitude;
    @Positive
    private double endRentLongitude;
}
