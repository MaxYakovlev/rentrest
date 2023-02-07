package ru.yakovlev.rentrest.model.dto.transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.dto.parking.ParkingDto;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransportDto {
    private Long id;
    private String name;
    private Integer charge;
    private Integer maxSpeed;
    private TransportConditionEnum condition;
    private String latitude;
    private String longitude;
    private Double distanceToUser;
    private TransportTypeEnum type;
    private TransportRentStatusEnum status;
    private ParkingDto parking;
}
