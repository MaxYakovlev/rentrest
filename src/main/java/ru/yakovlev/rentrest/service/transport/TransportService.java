package ru.yakovlev.rentrest.service.transport;

import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.entity.Transport;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

import java.util.List;
import java.util.Optional;

public interface TransportService {
    List<Transport> findAll(TransportTypeEnum type, TransportRentStatusEnum status, Long parkingId);
    Optional<Transport> findByNameAndStatus(String name, TransportRentStatusEnum transportRentStatus);
    Transport save(Transport transport, Long parkingId);
    Transport save(Transport transport);
    Transport delete(Long transportId);
    Transport findById(Long id);
    Transport updateRentStatus(Transport transport, TransportRentStatusEnum status);
    Transport update(Long transportId, Transport transport, Long parkingId);
    List<Transport> findByCoordinatesAndType(Double latitude, Double longitude, TransportTypeEnum transportType);
    void updateTransportCoords(String name, Double[] coords, TransportTypeEnum updateVehicleType, TransportRentStatusEnum updateVehicleRentStatus, String newTransportName, TransportConditionEnum newTransportCondition, Integer newCharge, Integer newMaxSpeed);
}
