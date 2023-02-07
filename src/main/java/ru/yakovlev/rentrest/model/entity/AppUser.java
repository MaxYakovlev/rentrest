package ru.yakovlev.rentrest.model.entity;

import lombok.Getter;
import lombok.Setter;
import ru.yakovlev.rentrest.model.enums.TelegramAction;
import ru.yakovlev.rentrest.model.enums.TransportTypeEnum;
import ru.yakovlev.rentrest.model.enums.UserRoleEnum;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "appuser")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    @Enumerated(EnumType.STRING)
    @Column(name = "telegram_transport_type")
    private TransportTypeEnum telegramTransportType;

    @Column(name = "telegram_transport_name")
    private String telegramTransportName;

    @Enumerated(EnumType.STRING)
    @Column(name = "telegram_action")
    private TelegramAction telegramAction;

    @Column(name = "chat_id")
    private String chatId;

    @OneToMany(mappedBy = "appUser")
    private List<Rent> rents = new ArrayList<>();
}
