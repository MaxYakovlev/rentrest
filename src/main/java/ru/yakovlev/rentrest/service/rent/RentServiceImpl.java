package ru.yakovlev.rentrest.service.rent;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yakovlev.rentrest.access.repository.RentRepository;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.model.context.UserContext;
import ru.yakovlev.rentrest.model.entity.AppUser;
import ru.yakovlev.rentrest.model.entity.Parking;
import ru.yakovlev.rentrest.model.entity.Rent;
import ru.yakovlev.rentrest.model.entity.Transport;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;
import ru.yakovlev.rentrest.security.SecurityContext;
import ru.yakovlev.rentrest.service.parking.ParkingService;
import ru.yakovlev.rentrest.service.transport.TransportService;
import ru.yakovlev.rentrest.service.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService{
    private final RentRepository rentRepository;
    private final UserService userService;
    private final TransportService transportService;
    private final ParkingService parkingService;

    @Value("${settings.tariff.electricScooter.base}")
    private BigDecimal electricScooterBaseCost;
    @Value("${settings.tariff.electricScooter.costPerMinute}")
    private BigDecimal electricScooterCostPerMinute;

    @Value("${settings.tariff.electricBicycle.base}")
    private BigDecimal bicycleBaseCost;
    @Value("${settings.tariff.electricBicycle.costPerMinute}")
    private BigDecimal bicycleCostPerMinute;

    @Override
    @Transactional
    public synchronized Rent createRent(AppUser appUser, Long transportId) {
        Rent rent = new Rent();

        SecurityContext.set(new UserContext(appUser.getId(), appUser.getRole()));

        if(!findUserRents(RentStatusEnum.OPEN).isEmpty()){
            throw new BusinessRuntimeException("Завершите открытую аренду", HttpStatus.BAD_REQUEST);
        }

        Transport transport = transportService.findById(transportId);

        if(transport.getStatus() == TransportRentStatusEnum.BUSY || transport.getStatus() == TransportRentStatusEnum.DELETED){
            throw new BusinessRuntimeException("Транспорт сейчас не доступен", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();

        rent.setStartRentDatetime(now);
        rent.setStatus(RentStatusEnum.OPEN);
        rent.setTransport(transport);
        rent.setAppUser(appUser);
        rent.setStartParking(transport.getParking());

        transportService.updateRentStatus(transport, TransportRentStatusEnum.BUSY);

        SecurityContext.clear();

        return rentRepository.save(rent);
    }

    @Override
    public Rent findById(Long id) {
        return rentRepository.findById(id).orElseThrow(() -> new AccessRuntimeException("Запись об аренде не найдена", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public synchronized Rent closeRent(AppUser appUser, Long rentId, Double lat, Double lon) {
        Rent rent = findById(rentId);
        Parking parking = parkingService.findParkingByCoordinates(lat, lon).orElseThrow(() -> new BusinessRuntimeException("Вы не в зоне парковки", HttpStatus.BAD_REQUEST));

        if(!rent.getAppUser().getId().equals(appUser.getId())){
            throw new BusinessRuntimeException("Нельзя закрыть чужую аренду", HttpStatus.BAD_REQUEST);
        }

        if(rent.getStatus() == RentStatusEnum.CLOSE){
            throw new BusinessRuntimeException("Аренда уже закрыта", HttpStatus.BAD_REQUEST);
        }

        if((parking.getType() == ParkingTypeEnum.ELECTRIC_BICYCLE && rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_SCOOTER)
                || (parking.getType() == ParkingTypeEnum.ELECTRIC_SCOOTER && rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_BICYCLE)
        ){
            throw new BusinessRuntimeException("Парковка не соответствует типу транспорта", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();

        Long minutes = ChronoUnit.MINUTES.between(rent.getStartRentDatetime(), now);
        if(minutes == 0){
            minutes = 1L;
        }
        BigDecimal fullCost = null;

        if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_SCOOTER){
            fullCost = electricScooterBaseCost.add(electricScooterCostPerMinute.multiply(BigDecimal.valueOf(minutes)));
        }

        if(rent.getTransport().getType() == TransportTypeEnum.ELECTRIC_BICYCLE){
            fullCost = bicycleBaseCost.add(bicycleCostPerMinute.multiply(BigDecimal.valueOf(minutes)));
        }

        Transport transport = transportService.findById(rent.getTransport().getId());
        transport.setStatus(TransportRentStatusEnum.FREE);
        transport.setParking(parking);
        transport.setLatitude(lat.toString());
        transport.setLongitude(lon.toString());
        transportService.save(transport);

        rent.setStatus(RentStatusEnum.CLOSE);
        rent.setAmount(fullCost);
        rent.setEndRentDatetime(now);
        rent.setEndParking(parking);

        return rentRepository.save(rent);
    }

    @Override
    public List<Rent> findUserRents(RentStatusEnum rentStatus) {
        AppUser appUser = userService.findById(SecurityContext.get().getId());

        return findRents(appUser, rentStatus);
    }

    @Override
    public Rent findUserRent(RentStatusEnum rentStatus, AppUser appUser) {
        Rent rent = new Rent();

        rent.setAppUser(appUser);

        if(rentStatus != null){
            rent.setStatus(rentStatus);
        }

        Example<Rent> rentExample = Example.of(rent);

        var foundRent = rentRepository.findOne(rentExample);
        var rentType = RentStatusEnum.OPEN  == rentStatus ? "открытой" : "закрытой";

        if(foundRent.isPresent()){
            return foundRent.get();
        }
        else{
            throw new AccessRuntimeException("У вас нет " + rentType + " аренды", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<Rent> findTelegramUserRents(String telegramUsername, RentStatusEnum rentStatus) {
        AppUser appUser = userService.findByUsername(telegramUsername);

        return findRents(appUser, rentStatus);
    }

    private List<Rent> findRents(AppUser appUser, RentStatusEnum rentStatus){
        Rent rent = new Rent();

        rent.setAppUser(appUser);

        if(rentStatus != null){
            rent.setStatus(rentStatus);
        }

        Example<Rent> rentExample = Example.of(rent);

        return rentRepository.findAll(rentExample);
    }

    @Override
    public List<Rent> findAll() {
        List<Rent> rents = rentRepository.findAll();
        rents.sort(Comparator.comparing(Rent::getId));
        Collections.reverse(rents);
        return rents;
    }

    @Override
    public Rent save(Rent rent){
        return rentRepository.save(rent);
    }

    @Override
    public Optional<Rent> findOpenByUserAndTransportName(AppUser appUser, String transportName) {
        Rent rent = new Rent();
        rent.setAppUser(appUser);
        rent.setStatus(RentStatusEnum.OPEN);

        Transport transport = new Transport();
        transport.setName(transportName);

        rent.setTransport(transport);

        Example<Rent> rentExample = Example.of(rent);

        return rentRepository.findOne(rentExample);
    }
}
