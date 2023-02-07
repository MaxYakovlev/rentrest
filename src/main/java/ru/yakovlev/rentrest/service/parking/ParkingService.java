package ru.yakovlev.rentrest.service.parking;

import ru.yakovlev.rentrest.model.entity.Parking;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;

import java.util.List;
import java.util.Optional;

public interface ParkingService {
    List<Parking> findAll();
    Parking findById(Long id);
    Parking save(Parking parking);
    Parking delete(Long parkingId);
    Parking update(Long id, Parking parking);
    Optional<Parking> findParkingByCoordinates(double lat, double lon);
    void updateCoordsArea(String name, Integer area, Double[] coords, String newParkingName, ParkingTypeEnum type, Boolean deletedParking);
}
