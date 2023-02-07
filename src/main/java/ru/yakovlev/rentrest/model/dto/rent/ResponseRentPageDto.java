package ru.yakovlev.rentrest.model.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseRentPageDto {
    private long totalElements;
    private int totalPages;
    private int size;
    private int numberOfElements;
    private List<RentDto> rents;
}
