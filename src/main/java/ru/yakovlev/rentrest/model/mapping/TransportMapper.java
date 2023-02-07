package ru.yakovlev.rentrest.model.mapping;

import org.mapstruct.Mapper;
import ru.yakovlev.rentrest.model.dto.transport.CreateTransportDto;
import ru.yakovlev.rentrest.model.dto.transport.TransportDto;
import ru.yakovlev.rentrest.model.dto.transport.UpdateTransportDto;
import ru.yakovlev.rentrest.model.entity.Transport;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransportMapper {
    List<TransportDto> modelToDto(List<Transport> model);
    TransportDto modelToDto(Transport model);
    Transport dtoToModel(CreateTransportDto dto);
    Transport dtoToModel(UpdateTransportDto dto);
}
