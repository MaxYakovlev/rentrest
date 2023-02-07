package ru.yakovlev.rentrest.model.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateParkingCoordsAreaDto {
    @NotNull
    @Positive
    private Integer area;
    @NotNull
    private Double[] coords;
    @NotBlank
    @NotEmpty
    private String name;

    private String newParkingName;

    @NotNull
    private ParkingTypeEnum type;

    private Boolean deletedParking;
}
