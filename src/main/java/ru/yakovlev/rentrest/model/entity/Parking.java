package ru.yakovlev.rentrest.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.DeleteStatusEnum;
import ru.yakovlev.rentrest.model.enums.ParkingTypeEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String longitude;

    private String latitude;

    private Double radius;

    @Enumerated(EnumType.STRING)
    private ParkingTypeEnum type;

    @Enumerated(EnumType.STRING)
    @Column(name = "delete_status")
    private DeleteStatusEnum deleteStatus;

    @OneToMany(mappedBy = "parking")
    private List<Transport> transports = new ArrayList<>();

    @OneToMany(mappedBy = "startParking")
    private List<Rent> startRents = new ArrayList<>();

    @OneToMany(mappedBy = "endParking")
    private List<Rent> endRents = new ArrayList<>();

    public Parking(String name, String longitude, String latitude, Double radius, ParkingTypeEnum type, DeleteStatusEnum deleteStatus) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.radius = radius;
        this.type = type;
        this.deleteStatus = deleteStatus;
    }
}
