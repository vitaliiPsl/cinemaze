package com.example.cinema.model.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MovieHallSeatDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Seat id", example = "21")
    private long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Seat row", example = "5", required = true)
    private int row;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, title = "Seat number", example = "14", required = true)
    private int number;
}
