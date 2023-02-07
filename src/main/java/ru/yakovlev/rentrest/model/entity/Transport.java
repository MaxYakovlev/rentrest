package ru.yakovlev.rentrest.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.TransportConditionEnum;
import ru.yakovlev.rentrest.model.enums.TransportRentStatusEnum;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Transport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer charge;

    private Integer maxSpeed;

    @Enumerated(EnumType.STRING)
    private TransportConditionEnum condition;

    private String latitude;

    private String longitude;

    @Enumerated(EnumType.STRING)
    private TransportTypeEnum type;

    @Transient
    private Double distanceToUser;

    @Enumerated(EnumType.STRING)
    private TransportRentStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "parking_id")
    private Parking parking;

    @OneToMany(mappedBy = "transport")
    private List<Rent> rents = new ArrayList<>();
}
