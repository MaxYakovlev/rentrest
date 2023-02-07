package ru.yakovlev.rentrest.model.dto.parking;

import lombok.Getter;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.DeleteStatusEnum;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateParkingDto {
    @NotNull
    @Size(max = 100, message = "Слишком длинное название парковки.")
    private String name;
    @Positive
    private double longitude;
    @Positive
    private double latitude;
    @Positive
    private double radius;
    @NotNull
    private ParkingTypeEnum type;
    @NotNull
    private DeleteStatusEnum deleteStatus;
}
