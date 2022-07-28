package com.example.cinema.web;

import com.example.cinema.model.dto.booking.BookingDto;
import com.example.cinema.model.entities.booking.Booking;
import com.example.cinema.model.errors.ApiError;
import com.example.cinema.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Booking API")
@AllArgsConstructor
@RestController
@RequestMapping("api/bookings")
public class BookingController {
    private final ModelMapper modelMapper;
    private final BookingService bookingService;

    @Operation(summary = "Make booking", description = "Returns DTO of made booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved"),
            @ApiResponse(responseCode = "400", description = "Isn't valid", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Movie session or seats were not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Booking information")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto makeBooking(@Valid @RequestBody BookingDto bookingDto, Authentication auth) {
        Booking booking = bookingService.makeBooking(auth, bookingDto.getMovieSessionId(), bookingDto.getSeatIds());

        return mapBookingToBookingDto(booking);
    }

    @Operation(summary = "Cancel booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully canceled"),
            @ApiResponse(responseCode = "404", description = "The booking was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @DeleteMapping("{id}")
    public void cancelBooking(
            @Parameter(description = "Booking id", example = "2")
            @PathVariable long id, Authentication authentication
    ){
        bookingService.cancelBooking(authentication, id);
    }

    @Operation(summary = "Get booking by id", description = "Returns booking dto, if found")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the booking"),
            @ApiResponse(responseCode = "404", description = "The booking was not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("{id}")
    public BookingDto getBooking(@PathVariable long id){
        Booking booking = bookingService.getBooking(id);

        return mapBookingToBookingDto(booking);
    }

    @Operation(summary = "Get booking", description = "Returns a list of bookings. Allows to filter by user or sessoin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found bookings"),
            @ApiResponse(responseCode = "404", description = "User or movie session were not found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping
    public List<BookingDto> getBookings(
            @Parameter(description = "Id of the user")
            @RequestParam(required = false) Long userId,
            @Parameter(description = "Id of the movie session")
            @RequestParam(required = false) Long sessionId) {
        List<Booking> bookings;

        if (userId != null) {
            bookings = bookingService.getBookingsByUser(userId);
        } else if (sessionId != null) {
            bookings = bookingService.getBookingsByMovieSession(sessionId);
        } else {
            bookings = bookingService.getAll();
        }

        return bookings.stream().map(this::mapBookingToBookingDto).collect(Collectors.toList());
    }

    private BookingDto mapBookingToBookingDto(Booking booking){
        return modelMapper.map(booking, BookingDto.class);
    }
}
