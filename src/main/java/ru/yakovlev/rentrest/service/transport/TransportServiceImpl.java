package ru.yakovlev.rentrest.service.transport;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yakovlev.rentrest.access.repository.TransportRepository;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Parking;
import ru.yakovlev.rentrest.model.entity.Transport;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;
import ru.yakovlev.rentrest.service.parking.ParkingService;
import ru.yakovlev.rentrest.utils.PositionHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransportServiceImpl implements TransportService{
    private final TransportRepository transportRepository;
    private final ParkingService parkingService;

    @Override
    @Transactional
    public List<Transport> findAll(TransportTypeEnum type, TransportRentStatusEnum status, Long parkingId) {
        Transport transport = new Transport();

        if(type != null){
            transport.setType(type);
        }

        if(status != null){
            transport.setStatus(status);
        }

        if(parkingId != null){
            Parking parking = parkingService.findById(parkingId);
            transport.setParking(parking);
        }

        Example<Transport> transportExample = Example.of(transport);

        return transportRepository.findAll(transportExample);
    }

    @Override
    public Optional<Transport> findByNameAndStatus(String name, TransportRentStatusEnum transportRentStatus) {
        Transport transport= new Transport();

        transport.setName(name);
        transport.setStatus(transportRentStatus);

        Example<Transport> transportExample = Example.of(transport);

        return transportRepository.findOne(transportExample);
    }

    @Override
    public Transport save(Transport transport, Long parkingId) {
        Parking parking = parkingService.findById(parkingId);

        double diffCoordinatesInMeters = PositionHelper.calcDiffCoordinatesInMeters(
                Double.parseDouble(parking.getLatitude()),
                Double.parseDouble(parking.getLongitude()),
                Double.parseDouble(transport.getLatitude()),
                Double.parseDouble(transport.getLongitude()));

        if(diffCoordinatesInMeters > parking.getRadius()){
            throw new BusinessRuntimeException("Внесите транспорт в зону парковки", HttpStatus.BAD_REQUEST);
        }

        if(parking.getType() == ParkingTypeEnum.ALL
            || (parking.getType() == ParkingTypeEnum.ELECTRIC_SCOOTER && transport.getType() == TransportTypeEnum.ELECTRIC_SCOOTER)
            || (parking.getType() == ParkingTypeEnum.ELECTRIC_BICYCLE && transport.getType() == TransportTypeEnum.ELECTRIC_BICYCLE)
        ) {
            transport.setParking(parking);
        }
        else{
            throw new BusinessRuntimeException("Неправильные параметры транспорта", HttpStatus.BAD_REQUEST);
        }

        return transportRepository.save(transport);
    }

    @Override
    public Transport save(Transport transport) {
        return transportRepository.save(transport);
    }

    @Override
    public Transport delete(Long transportId) {
        Transport transport = findById(transportId);

        if(transport.getStatus() == TransportRentStatusEnum.DELETED){
            throw new BusinessRuntimeException("Транспорт уже удален", HttpStatus.BAD_REQUEST);
        }

        transport.setStatus(TransportRentStatusEnum.DELETED);

        return transportRepository.save(transport);
    }

    @Override
    public Transport findById(Long id) {
        return transportRepository.findById(id).orElseThrow(() -> new AccessRuntimeException("Транспорт не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public Transport updateRentStatus(Transport transport, TransportRentStatusEnum status) {
        transport.setStatus(status);

        return transportRepository.save(transport);
    }

    @Override
    public Transport update(Long transportId, Transport transport, Long parkingId) {
        Transport oldTransport = findById(transportId);

        if(transport.getName() != null){
            oldTransport.setName(transport.getName());
        }

        if(transport.getCharge() != null){
            oldTransport.setCharge(transport.getCharge());
        }

        if(transport.getMaxSpeed() != null){
            oldTransport.setMaxSpeed(transport.getMaxSpeed());
        }

        if(transport.getCondition() != null){
            oldTransport.setCondition(transport.getCondition());
        }

        if(transport.getLatitude() != null){
            oldTransport.setLatitude(transport.getLatitude());
        }

        if(transport.getLongitude() != null){
            oldTransport.setLongitude(transport.getLongitude());
        }

        if(transport.getType() != null){
            oldTransport.setType(transport.getType());
        }

        if(transport.getStatus() != null){
            oldTransport.setStatus(transport.getStatus());
        }

        if(parkingId != null){
            Parking parking = parkingService.findById(parkingId);
            oldTransport.setParking(parking);
        }

        return transportRepository.save(oldTransport);
    }

    @Override
    public List<Transport> findByCoordinatesAndType(Double latitude, Double longitude, TransportTypeEnum transportType) {
        Transport transport = new Transport();
        transport.setType(transportType);
        transport.setStatus(TransportRentStatusEnum.FREE);

        Example<Transport> transportExample = Example.of(transport);

        List<Transport> transports = transportRepository.findAll(transportExample);

        for(Transport item: transports){
            double distance = PositionHelper.calcDiffCoordinatesInMeters(latitude, longitude, Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude()));
            item.setDistanceToUser(distance);
        }

        transports.sort(Comparator.comparing(Transport::getDistanceToUser));

        transports = transports.stream().filter(x -> x.getDistanceToUser() <= 1000).collect(Collectors.toList());

        return transports;
    }

    @Override
    public void updateTransportCoords(String name, Double[] coords, TransportTypeEnum updateVehicleType, TransportRentStatusEnum updateVehicleRentStatus, String newTransportName, TransportConditionEnum newTransportCondition, Integer newCharge, Integer newMaxSpeed) {
        var parkingOptional = parkingService.findParkingByCoordinates(coords[0], coords[1]);

        if(parkingOptional.isEmpty()){
            throw new BusinessRuntimeException("Перенесите транпорт в зону парковки", HttpStatus.BAD_REQUEST);
        }

        Parking parking = parkingOptional.get();
        Transport transport = transportRepository.findByName(name);

        if((transport.getType() == TransportTypeEnum.ELECTRIC_BICYCLE && parking.getType() == ParkingTypeEnum.ELECTRIC_SCOOTER)
            || (updateVehicleType == TransportTypeEnum.ELECTRIC_BICYCLE && parking.getType() == ParkingTypeEnum.ELECTRIC_SCOOTER)
            || (transport.getType() == TransportTypeEnum.ELECTRIC_SCOOTER && parking.getType() == ParkingTypeEnum.ELECTRIC_BICYCLE)
            || (updateVehicleType == TransportTypeEnum.ELECTRIC_SCOOTER && parking.getType() == ParkingTypeEnum.ELECTRIC_BICYCLE)
        ){
            throw new BusinessRuntimeException("Неподходящий тип парковки", HttpStatus.BAD_REQUEST);
        }

        if(updateVehicleRentStatus != null){
            transport.setStatus(updateVehicleRentStatus);
        }

        if(updateVehicleType != null){
            transport.setType(updateVehicleType);
        }

        if(newTransportCondition != null){
            transport.setCondition(newTransportCondition);
        }

        if(newTransportName != null && !newTransportName.isEmpty()){
            transport.setName(newTransportName);
        }
        else{
            throw new BusinessRuntimeException("Новое имя транспорта не может быть пустым", HttpStatus.BAD_REQUEST);
        }

        transport.setParking(parking);
        transport.setLatitude(coords[0].toString());
        transport.setLongitude(coords[1].toString());
        transport.setCharge(newCharge);
        transport.setMaxSpeed(newMaxSpeed);

        transportRepository.save(transport);
    }
}
