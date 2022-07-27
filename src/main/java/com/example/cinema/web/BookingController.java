package com.example.cinema.web;

import com.example.cinema.model.dto.booking.BookingDto;
import com.example.cinema.model.entities.booking.Booking;
import com.example.cinema.service.BookingService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("api/bookings")
public class BookingController {
    private final ModelMapper modelMapper;
    private final BookingService bookingService;

    @PostMapping
    public BookingDto makeBooking(@Valid @RequestBody BookingDto bookingDto, Authentication auth) {
        Booking booking = bookingService.makeBooking(auth, bookingDto.getMovieSessionId(), bookingDto.getSeatIds());

        return mapBookingToBookingDto(booking);
    }

    @DeleteMapping("{id}")
    public void cancelBooking(@PathVariable long id, Authentication authentication){
        bookingService.cancelBooking(authentication, id);
    }

    @GetMapping("{id}")
    public BookingDto getBooking(@PathVariable long id){
        Booking booking = bookingService.getBooking(id);

        return mapBookingToBookingDto(booking);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestParam(required = false) Long userId, @RequestParam(required = false) Long sessionId) {
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
