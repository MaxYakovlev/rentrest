package ru.yakovlev.rentrest.access.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yakovlev.rentrest.model.entity.Rent;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentRepository extends JpaRepository<Rent, Long> {
    Optional<List<Rent>> findByAppUserId(Long userId);
}
