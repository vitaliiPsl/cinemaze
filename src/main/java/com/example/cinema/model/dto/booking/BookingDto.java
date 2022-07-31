package com.example.cinema.model.dto.booking;

import com.example.cinema.model.dto.session.MovieSessionDto;
import com.example.cinema.model.dto.session.MovieSessionSeatDto;
import com.example.cinema.model.dto.user.UserDto;
import com.example.cinema.model.entities.booking.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class BookingDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Booking id", example = "4")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Booking time", description = "The time of the booking", example = "2022-07-29T18:00")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime bookedAt;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Booking status", description = "Status of the booking", example = "CANCELED", implementation = BookingStatus.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingStatus bookingStatus;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Total price", description = "Total price of the booking", example = "17.50")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private double totalPrice;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "User", description = "User who made the booking", implementation = UserDto.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto user;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Movie session id", description = "Id of the movie session", example = "2", required = true)
    @NotNull(message = "You have to provide id of the movie session")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long movieSessionId;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Movie session", implementation = MovieSessionDto.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonIgnoreProperties("seats")
    private MovieSessionDto movieSession;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, title = "Seat ids", description = "List of seat ids that user wants to book", example = "[13, 14, 15]", required = true)
    @Size(min = 1, message = "You have to choose at least one seat")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Long> seatIds = new HashSet<>();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Booked seats", description = "List of booked seats", implementation = MovieSessionSeatDto.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<MovieSessionSeatDto> seats = new HashSet<>();

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

