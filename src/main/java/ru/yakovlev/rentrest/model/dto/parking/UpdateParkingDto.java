package ru.yakovlev.rentrest.model.dto.parking;

import lombok.Getter;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.DeleteStatusEnum;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;

@Getter
@Setter
public class UpdateParkingDto {
    private String name;
    private String longitude;
    private String latitude;
    private Double radius;
    private ParkingTypeEnum type;
    private DeleteStatusEnum deleteStatus;
}
