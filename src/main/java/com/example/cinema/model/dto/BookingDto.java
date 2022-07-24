package com.example.cinema.model.dto;

import com.example.cinema.model.entities.User;
import com.example.cinema.model.entities.booking.BookingStatus;
import com.example.cinema.model.entities.movie.MovieSession;
import com.example.cinema.model.entities.movie.MovieSessionSeat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class BookingDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime bookedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingStatus bookingStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double totalPrice;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("bookings")
    private User user;

    @NotNull(message = "You have to provide id of the movie session")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long movieSessionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"bookings", "bookedSeats"})
    private MovieSession movieSession;

    @Size(min = 1, message = "You have to choose at least one seat")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Long> seatIds = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties({"booking", "movieSession"})
    private Set<MovieSessionSeat> seats = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDto that = (BookingDto) o;
        return movieSessionId == that.movieSessionId && Objects.equals(user, that.user) && Objects.equals(movieSession, that.movieSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, movieSessionId, movieSession);
    }
}

