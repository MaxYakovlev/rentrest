package ru.yakovlev.rentrest.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yakovlev.rentrest.model.entity.Transport;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {
    Transport findByName(String name);
}
