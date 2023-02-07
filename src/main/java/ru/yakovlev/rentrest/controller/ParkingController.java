package ru.yakovlev.rentrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.model.dto.parking.CreateParkingDto;
import ru.yakovlev.rentrest.model.dto.parking.ParkingDto;
import ru.yakovlev.rentrest.model.dto.parking.UpdateParkingCoordsAreaDto;
import ru.yakovlev.rentrest.model.dto.parking.UpdateParkingDto;
import ru.yakovlev.rentrest.model.entity.Parking;
import ru.yakovlev.rentrest.model.enums.DeleteStatusEnum;
import ru.yakovlev.rentrest.model.mapping.ParkingMapper;
import ru.yakovlev.rentrest.service.parking.ParkingService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parkings")
@RequiredArgsConstructor
public class ParkingController {
    private final ParkingService parkingService;
    private final ParkingMapper parkingMapper;

    @GetMapping
    public List<ParkingDto> findAllParkings(){
        List<Parking> parkings = parkingService
                .findAll()
                .stream()
                .filter(parking -> parking.getDeleteStatus().equals(DeleteStatusEnum.NOT_DELETED))
                .collect(Collectors.toList());

        return parkingMapper.modelToDto(parkings);
    }

    @PostMapping
    public ParkingDto createParking(@Valid @RequestBody CreateParkingDto createParkingDto){
        Parking parking = parkingMapper.dtoToModel(createParkingDto);

        Parking savedParking = parkingService.save(parking);

        if(savedParking == null){
            throw new AccessRuntimeException("Парковка не добавлена", HttpStatus.BAD_REQUEST);
        }

        return parkingMapper.modelToDto(savedParking);
    }

    @DeleteMapping("/{id}")
    public ParkingDto deleteParkingById(@PathVariable Long id){
        Parking deletedParking = parkingService.delete(id);

        if(deletedParking == null){
            throw new AccessRuntimeException("Парковка не удалена", HttpStatus.NOT_MODIFIED);
        }

        return parkingMapper.modelToDto(deletedParking);
    }

    @PatchMapping("/{id}")
    public ParkingDto updateParkingById(@PathVariable Long id, @RequestBody UpdateParkingDto updateParkingDto){
        Parking parking = parkingMapper.dtoToModel(updateParkingDto);

        Parking savedParking = parkingService.update(id, parking);

        if(savedParking == null){
            throw new AccessRuntimeException("Изменения не сохранены", HttpStatus.NOT_MODIFIED);
        }

        return parkingMapper.modelToDto(savedParking);
    }

    @PatchMapping("/update-coords-area")
    public HttpStatus updateParkingCoordsArea(@Valid @RequestBody UpdateParkingCoordsAreaDto updateParkingCoordsAreaDto){
        parkingService.updateCoordsArea(
                updateParkingCoordsAreaDto.getName(),
                updateParkingCoordsAreaDto.getArea(),
                updateParkingCoordsAreaDto.getCoords(),
                updateParkingCoordsAreaDto.getNewParkingName(),
                updateParkingCoordsAreaDto.getType(),
                updateParkingCoordsAreaDto.getDeletedParking()
        );

        return HttpStatus.OK;
    }
}
