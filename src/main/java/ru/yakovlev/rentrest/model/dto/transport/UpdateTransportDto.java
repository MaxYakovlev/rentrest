package ru.yakovlev.rentrest.model.dto.transport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransportDto {
    @Size(max = 10)
    private String name;

    private Integer charge;

    private Integer maxSpeed;

    private TransportConditionEnum condition;

    private Double latitude;

    private Double longitude;

    private TransportTypeEnum type;

    private TransportRentStatusEnum status;

    private Long parkingId;

    private boolean transportTypeChargeMaxSpeedValidTrue;

    @AssertTrue
    public boolean isTransportTypeChargeMaxSpeedValidTrue(){

        if(type == null){
            return true;
        }

        if(type == TransportTypeEnum.ELECTRIC_BICYCLE && charge == null && maxSpeed == null){
            return true;
        }

        if(type == TransportTypeEnum.ELECTRIC_SCOOTER && charge != null && charge >= 0 && maxSpeed != null && maxSpeed >= 0){
            return true;
        }

        return false;
    }
}
