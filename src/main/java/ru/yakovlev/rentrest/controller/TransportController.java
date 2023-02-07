package ru.yakovlev.rentrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.model.dto.transport.CreateTransportDto;
import ru.yakovlev.rentrest.model.dto.transport.TransportDto;
import ru.yakovlev.rentrest.model.dto.transport.UpdateTransportCoordsDto;
import ru.yakovlev.rentrest.model.dto.transport.UpdateTransportDto;
import ru.yakovlev.rentrest.model.entity.Transport;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;
import ru.yakovlev.rentrest.model.mapping.TransportMapper;
import ru.yakovlev.rentrest.service.transport.TransportService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportController {
    private final TransportService transportService;
    private final TransportMapper transportMapper;

    @GetMapping
    public List<TransportDto> findAllTransports(
            @RequestParam(required = false) TransportTypeEnum type,
            @RequestParam(required = false) TransportRentStatusEnum status,
            @RequestParam(required = false) Long parkingId){

        List<Transport> transports = transportService
                .findAll(type, status, parkingId)
                .stream()
                .filter(x -> !x.getStatus().equals(TransportRentStatusEnum.DELETED))
                .collect(Collectors.toList());

        return transportMapper.modelToDto(transports);
    }

    @GetMapping("/{id}")
    public TransportDto findTransportById(@PathVariable Long id){
        Transport transport = transportService.findById(id);

        return transportMapper.modelToDto(transport);
    }

    @DeleteMapping("/{id}")
    public TransportDto deleteTransportById(@PathVariable Long id){
        Transport deletedTransport = transportService.delete(id);

        return transportMapper.modelToDto(deletedTransport);
    }

    @PostMapping
    public TransportDto createTransport(@Valid @RequestBody CreateTransportDto createTransportDto){
        Transport transport = transportMapper.dtoToModel(createTransportDto);

        Transport savedTransport = transportService.save(transport, createTransportDto.getParkingId());

        if(savedTransport == null){
            throw new AccessRuntimeException("Транспорт не добавлен", HttpStatus.BAD_REQUEST);
        }

        return transportMapper.modelToDto(savedTransport);
    }

    @PatchMapping("/{id}")
    public TransportDto updateTransport(@PathVariable Long id, @Valid @RequestBody UpdateTransportDto updateTransportDto){
        Transport transport = transportMapper.dtoToModel(updateTransportDto);

        Transport updatedTransport = transportService.update(id, transport, updateTransportDto.getParkingId());

        if(updatedTransport == null){
            throw new AccessRuntimeException("Транспорт не обновлен", HttpStatus.NOT_MODIFIED);
        }

        return transportMapper.modelToDto(updatedTransport);
    }

    @PatchMapping("/update-coords")
    public HttpStatus updateTransportCoords(@Valid @RequestBody UpdateTransportCoordsDto updateTransportDto){
        transportService.updateTransportCoords(
                updateTransportDto.getName(),
                updateTransportDto.getCoords(),
                updateTransportDto.getUpdateVehicleType(),
                updateTransportDto.getUpdateVehicleRentStatus(),
                updateTransportDto.getNewTransportName(),
                updateTransportDto.getNewTransportCondition(),
                updateTransportDto.getNewCharge(),
                updateTransportDto.getNewMaxSpeed()
        );

        return HttpStatus.OK;
    }
}
