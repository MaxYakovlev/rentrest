package ru.yakovlev.rentrest.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.RentStatusEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Rent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_rent_datetime")
    private LocalDateTime startRentDatetime;

    @Column(name = "end_rent_datetime")
    private LocalDateTime endRentDatetime;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private RentStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "start_parking_id")
    private Parking startParking;

    @ManyToOne
    @JoinColumn(name = "end_parking_id")
    private Parking endParking;

    @ManyToOne
    @JoinColumn(name = "appuser_id")
    private AppUser appUser;

    @ManyToOne
    @JoinColumn(name = "transport_id")
    private Transport transport;
}
