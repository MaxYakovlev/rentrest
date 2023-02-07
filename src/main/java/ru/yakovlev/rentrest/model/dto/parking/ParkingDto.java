package ru.yakovlev.rentrest.model.dto.parking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.DeleteStatusEnum;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;

@Getter
@Setter
@AllArgsConstructor
public class ParkingDto {
    private Long id;
    private String name;
    private String longitude;
    private String latitude;
    private Double radius;
    private ParkingTypeEnum type;
    private DeleteStatusEnum deleteStatus;
}
