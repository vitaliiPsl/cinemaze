package com.example.cinema.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class MovieHallDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long id;

    @Min(1)
    private int numberOfSeatRows;

    @Min(1)
    private int numberOfSeatsPerRow;
}
