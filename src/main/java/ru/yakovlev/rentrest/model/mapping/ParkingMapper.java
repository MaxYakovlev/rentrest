package ru.yakovlev.rentrest.model.mapping;

import org.mapstruct.Mapper;
import ru.yakovlev.rentrest.model.dto.parking.CreateParkingDto;
import ru.yakovlev.rentrest.model.dto.parking.ParkingDto;
import ru.yakovlev.rentrest.model.dto.parking.UpdateParkingDto;
import ru.yakovlev.rentrest.model.entity.Parking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParkingMapper {
    List<ParkingDto> modelToDto(List<Parking> model);
    ParkingDto modelToDto(Parking model);
    Parking dtoToModel(CreateParkingDto dto);
    Parking dtoToModel(UpdateParkingDto dto);
}
