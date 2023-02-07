package ru.yakovlev.rentrest.model.dto.transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransportCoordsDto {
    @NotNull
    private Double[] coords;
    @NotNull
    @NotBlank
    private String name;

    private TransportTypeEnum updateVehicleType;

    private TransportRentStatusEnum updateVehicleRentStatus;

    private String newTransportName;

    private TransportConditionEnum newTransportCondition;

    private Integer newCharge;

    private Integer newMaxSpeed;
}
