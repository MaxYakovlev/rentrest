package ru.yakovlev.rentrest.model.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.dto.parking.ParkingDto;
import ru.yakovlev.rentrest.model.dto.transport.TransportDto;
import ru.yakovlev.rentrest.model.dto.user.UserDto;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentDto {
    private Long id;
    private LocalDateTime startRentDatetime;
    private LocalDateTime endRentDatetime;
    private BigDecimal amount;
    private ParkingDto startParking;
    private ParkingDto endParking;
    private RentStatusEnum status;
    private UserDto appUser;
    private TransportDto transport;
}
