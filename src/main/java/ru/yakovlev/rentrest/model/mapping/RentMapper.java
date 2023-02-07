package ru.yakovlev.rentrest.model.mapping;

import org.mapstruct.Mapper;
import ru.yakovlev.rentrest.model.dto.rent.CreateRentDto;
import ru.yakovlev.rentrest.model.dto.rent.RentDto;
import ru.yakovlev.rentrest.model.entity.Rent;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RentMapper {
    RentDto modelToDto(Rent model);
    Rent dtoToModel(CreateRentDto dto);
    List<RentDto> modelToDto(List<Rent> model);
}