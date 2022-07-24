package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.booking.Booking;
import com.example.cinema.model.entities.booking.BookingStatus;
import com.example.cinema.model.entities.session.MovieSession;
import com.example.cinema.model.entities.session.MovieSessionSeat;
import com.example.cinema.persistence.BookingRepository;
import com.example.cinema.persistence.MovieSessionSeatRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final MovieSessionSeatRepository sessionSeatRepository;

    private final UserService userService;
    private final MovieSessionService sessionService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Booking makeBooking(Authentication auth, long movieSessionId, Set<Long> seatIds) {
        log.debug("User: {} books seats {} for session {}", auth.getName(), seatIds, movieSessionId);

        Booking booking = new Booking();

        String username = auth.getName();
        User user = userService.getUser(username);
        booking.setUser(user);

        MovieSession movieSession = sessionService.getMovieSession(movieSessionId);
        booking.setMovieSession(movieSession);

        Set<MovieSessionSeat> seats = getSeats(seatIds, booking, movieSession);
        booking.setSeats(seats);

        double totalPrice = calculateTotalPrice(movieSession.getPrice(), seats.size());
        booking.setTotalPrice(totalPrice);

        booking.setBookingStatus(BookingStatus.COMPLETED);

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Authentication auth, long bookingId) {
        log.debug("Cancel booking: {}", bookingId);

        String username = auth.getName();
        User user = userService.getUser(username);

        Booking booking = getBooking(bookingId);

        if (!booking.getUser().equals(user)) {
            log.error("User: {} cannot cancel this booking: {}", user, booking);
            throw new IllegalStateException("Booking can be cancelled only by user who made booking");
        }

        booking.setBookingStatus(BookingStatus.CANCELED);

        for (MovieSessionSeat seat : booking.getSeats()) {
            seat.setBooked(false);
            seat.setBooking(null);
        }

        return booking;
    }

    @Transactional(readOnly = true)
    public Booking getBooking(long id) {
        log.debug("Get booking by id: {}", id);

        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Booking.class));
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByMovieSession(long movieSessionId) {
        log.debug("Get bookings by movie session: {}", movieSessionId);

        MovieSession movieSession = sessionService.getMovieSession(movieSessionId);

        return bookingRepository.findByMovieSession(movieSession);
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUser(long userId) {
        log.debug("Get bookings by user: {}", userId);

        User user = userService.getUser(userId);

        return bookingRepository.findByUser(user);
    }

    @Transactional(readOnly = true)
    public List<Booking> getAll() {
        log.debug("Get all bookings");

        return bookingRepository.findAll();
    }

    private double calculateTotalPrice(double price, int numberOfSeats) {
        return price * numberOfSeats;
    }

    private Set<MovieSessionSeat> getSeats(Set<Long> seatIds, Booking booking, MovieSession movieSession) {
        Set<MovieSessionSeat> seats = new HashSet<>();

        for (var seatId : seatIds) {
            MovieSessionSeat seat = getSeat(seatId, movieSession);
            seat.setBooking(booking);
            seats.add(seat);
        }

        return seats;
    }

    private MovieSessionSeat getSeat(long id, MovieSession movieSession) {
        MovieSessionSeat seat = sessionSeatRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieSessionSeat.class));

        if (!seat.getMovieSession().equals(movieSession)) {
            log.error("Seat is from other movie session: {}", seat.getMovieSession());
            throw new IllegalStateException(String.format("Wrong movie session: %d", seat.getMovieSession().getId()));
        }

        if (seat.isBooked()) {
            log.error("Seat {} is already booked", seat);
            throw new IllegalStateException(String.format("Seat %d is already booked", seat.getId()));
        }

        seat.setBooked(true);

        return seat;
    }
}
