package ru.yakovlev.rentrest.model.dto.transport;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransportDto {
    @NotNull
    @NotBlank
    @Size(max = 10)
    private String name;

    @NotNull
    private Integer charge;

    @NotNull
    private Integer maxSpeed;

    @NotNull
    private TransportConditionEnum condition;

    @Positive
    private double latitude;

    @Positive
    private double longitude;

    @NotNull
    private TransportTypeEnum type;

    @NotNull
    private TransportRentStatusEnum status;

    @Positive
    @NotNull
    private Long parkingId;

    private boolean transportTypeChargeMaxSpeedValidTrue;

    @AssertTrue
    public boolean isTransportTypeChargeMaxSpeedValidTrue(){
        if((type == TransportTypeEnum.ELECTRIC_SCOOTER || type == TransportTypeEnum.ELECTRIC_BICYCLE) && charge != null && charge >= 0 && maxSpeed != null && maxSpeed >= 0){
            return true;
        }

        return false;
    }
}
