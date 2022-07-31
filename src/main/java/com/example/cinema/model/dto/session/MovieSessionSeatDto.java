package com.example.cinema.model.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MovieSessionSeatDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Seat id", example = "21")
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Booked", example = "true")
    private boolean booked;

    @Schema(
            accessMode = Schema.AccessMode.READ_ONLY,
            title = "Hall seats",
            description = "Corresponding hall seat",
            implementation = MovieHallSeatDto.class
    )
    private MovieHallSeatDto hallSeat;
}
