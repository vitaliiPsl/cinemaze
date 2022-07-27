package com.example.cinema.model.dto.session;

import lombok.Data;

@Data
public class MovieHallSeatDto {
    private long id;

    private int row;
    private int number;
}
