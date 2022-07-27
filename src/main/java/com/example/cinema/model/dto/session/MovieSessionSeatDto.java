package com.example.cinema.model.dto.session;

import com.example.cinema.model.dto.booking.BookingDto;
import lombok.Data;


@Data
public class MovieSessionSeatDto {
    private long id;
    private boolean booked;

    private MovieHallSeatDto hallSeat;

    private MovieSessionDto movieSession;

    private BookingDto booking;
}
