package ru.yakovlev.rentrest.service.parking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yakovlev.rentrest.access.repository.ParkingRepository;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.model.entity.Parking;
import ru.yakovlev.rentrest.model.enums.DeleteStatusEnum;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;
import ru.yakovlev.rentrest.utils.PositionHelper;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingServiceImpl implements ParkingService{
    private final ParkingRepository parkingRepository;

    @Override
    public List<Parking> findAll() {
        return parkingRepository.findAll();
    }

    public Optional<Parking> findParkingByCoordinates(double lat, double lon){

        return findAll().stream()
                .filter(x ->
                        PositionHelper.calcDiffCoordinatesInMeters(
                                lat, lon, Double.parseDouble(x.getLatitude()), Double.parseDouble(x.getLongitude())
                        )
                                <= x.getRadius()
                )
                .findFirst();
    }

    @Override
    public void updateCoordsArea(String name, Integer area, Double[] coords, String newParkingName, ParkingTypeEnum type, Boolean deletedParking) {
        var parkingOptional = parkingRepository.findByName(name);

        if(parkingOptional.isEmpty()){
            throw new BusinessRuntimeException("Нет парковки с таким названием", HttpStatus.BAD_REQUEST);
        }

        var parking = parkingOptional.get();

        parking.setRadius(area.doubleValue());
        parking.setLatitude(coords[0].toString());
        parking.setLongitude(coords[1].toString());
        parking.setType(type);

        if(!parking.getName().equals(newParkingName)){
            parking.setName(newParkingName);
        }

        if(deletedParking){
            parking.setDeleteStatus(DeleteStatusEnum.DELETED);
        }
        else {
            parking.setDeleteStatus(DeleteStatusEnum.NOT_DELETED);
        }

        parkingRepository.save(parking);
    }

    @Override
    public Parking findById(Long id) {
        return parkingRepository.findById(id).orElseThrow(() -> new AccessRuntimeException("Парковка не найдена", HttpStatus.NOT_FOUND));
    }

    @Override
    public Parking save(Parking parking) {
        return parkingRepository.save(parking);
    }

    @Override
    public Parking delete(Long parkingId) {
        Parking parking = findById(parkingId);

        if(parking.getDeleteStatus() == DeleteStatusEnum.DELETED){
            throw new AccessRuntimeException("Парковка уже удалена", HttpStatus.BAD_REQUEST);
        }

        parking.setDeleteStatus(DeleteStatusEnum.DELETED);

        return save(parking);
    }

    @Override
    public Parking update(Long id, Parking parking) {
        Parking existsParking = parkingRepository.findById(id)
                .orElseThrow(() -> new AccessRuntimeException("Парковка не найдена", HttpStatus.NOT_FOUND));

        if(parking.getName() != null){
            existsParking.setName(parking.getName());
        }

        if(parking.getLongitude() != null){
            existsParking.setLongitude(parking.getLongitude());
        }

        if(parking.getLatitude() != null){
            existsParking.setLatitude(parking.getLatitude());
        }

        if(parking.getRadius() != null){
            existsParking.setRadius(parking.getRadius());
        }

        if(parking.getType() != null){
            existsParking.setType(parking.getType());
        }

        if(parking.getDeleteStatus() != null){
            existsParking.setDeleteStatus(parking.getDeleteStatus());
        }

        return save(existsParking);
    }
}
