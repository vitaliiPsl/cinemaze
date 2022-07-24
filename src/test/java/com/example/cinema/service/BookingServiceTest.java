package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.booking.Booking;
import com.example.cinema.model.entities.booking.BookingStatus;
import com.example.cinema.model.entities.session.MovieHallSeat;
import com.example.cinema.model.entities.session.MovieSession;
import com.example.cinema.model.entities.session.MovieSessionSeat;
import com.example.cinema.persistence.BookingRepository;
import com.example.cinema.persistence.MovieSessionSeatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    MovieSessionSeatRepository seatRepository;

    @Mock
    UserService userService;

    @Mock
    MovieSessionService sessionService;

    @InjectMocks
    BookingService bookingService;

    @Test
    void testMakeBooking() {
        // given
        String email = "user@mail.com";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        User user = getUser(1, email);

        MovieSessionSeat seat1 = getMovieSessionSeat(1, false, getHallSeat(1, 1, 1));
        MovieSessionSeat seat2 = getMovieSessionSeat(2, false, getHallSeat(1, 1, 2));
        Set<Long> seatIds = Set.of(seat1.getId(), seat2.getId());

        long sessionId = 5;
        double sessionPrice = 10;
        MovieSession movieSession = getMovieSession(sessionId, sessionPrice, Set.of(seat1, seat2));

        seat1.setMovieSession(movieSession);
        seat2.setMovieSession(movieSession);

        // when
        when(userService.getUser(email)).thenReturn(user);
        when(sessionService.getMovieSession(sessionId)).thenReturn(movieSession);
        when(seatRepository.findById(seat1.getId())).thenReturn(Optional.of(seat1));
        when(seatRepository.findById(seat2.getId())).thenReturn(Optional.of(seat2));
        when(bookingRepository.save(any(Booking.class))).then(returnsFirstArg());

        Booking result = bookingService.makeBooking(authToken, sessionId, seatIds);

        // then
        verify(userService).getUser(email);
        verify(sessionService).getMovieSession(sessionId);
        verify(bookingRepository).save(ArgumentMatchers.any(Booking.class));

        assertThat(result.getUser(), is(user));
        assertThat(result.getMovieSession(), is(movieSession));
        assertThat(result.getTotalPrice(), is(sessionPrice * seatIds.size()));
        assertThat(result.getBookingStatus(), is(BookingStatus.COMPLETED));

        Set<MovieSessionSeat> seats = result.getSeats();
        assertThat(seats, containsInAnyOrder(seat1, seat2));
    }

    @Test
    void testMakeBookingThrowsExceptionIfSeatNotFound() {
        // given
        String email = "user@mail.com";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        User user = getUser(1, email);

        MovieSessionSeat seat1 = getMovieSessionSeat(1, false, getHallSeat(1, 1, 1));
        Set<Long> seatIds = Set.of(seat1.getId());

        long sessionId = 5;
        double sessionPrice = 10;
        MovieSession movieSession = getMovieSession(sessionId, sessionPrice, Set.of());

        // when
        when(userService.getUser(email)).thenReturn(user);
        when(sessionService.getMovieSession(sessionId)).thenReturn(movieSession);
        when(seatRepository.findById(seat1.getId())).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> bookingService.makeBooking(authToken, sessionId, seatIds));

        verify(userService).getUser(email);
        verify(sessionService).getMovieSession(sessionId);
    }

    @Test
    void testMakeBookingThrowsExceptionIfSeatIsFromOtherMovieSession() {
        // given
        String email = "user@mail.com";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        User user = getUser(1, email);

        MovieSessionSeat seat = getMovieSessionSeat(1, false, getHallSeat(1, 1, 1));
        Set<Long> seatIds = Set.of(seat.getId());

        long sessionId = 5;
        double sessionPrice = 10;
        MovieSession movieSession = getMovieSession(sessionId, sessionPrice, Set.of());
        movieSession.setStartsAt(LocalDateTime.now());

        // seat belongs to different session
        seat.setMovieSession(getMovieSession(13, 10, Set.of(seat)));

        // when
        when(userService.getUser(email)).thenReturn(user);
        when(sessionService.getMovieSession(sessionId)).thenReturn(movieSession);
        when(seatRepository.findById(seat.getId())).thenReturn(Optional.of(seat));

        // then
        assertThrows(RuntimeException.class, () -> bookingService.makeBooking(authToken, sessionId, seatIds));
        verify(userService).getUser(email);
        verify(sessionService).getMovieSession(sessionId);
    }


    @Test
    void testMakeBookingThrowsExceptionIfSeatIsAlreadyBooked() {
        // given
        String email = "user@mail.com";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        User user = getUser(1, email);

        MovieSessionSeat seat1 = getMovieSessionSeat(1, true, getHallSeat(1, 1, 1));
        Set<Long> seatIds = Set.of(seat1.getId());

        long sessionId = 5;
        double sessionPrice = 10;
        Set<MovieSessionSeat> seats = Set.of(seat1);
        MovieSession movieSession = getMovieSession(sessionId, sessionPrice, seats);

        seat1.setMovieSession(movieSession);

        // when
        when(userService.getUser(email)).thenReturn(user);
        when(sessionService.getMovieSession(sessionId)).thenReturn(movieSession);
        when(seatRepository.findById(seat1.getId())).thenReturn(Optional.of(seat1));

        // then
        assertThrows(RuntimeException.class, () -> bookingService.makeBooking(authToken, sessionId, seatIds));
        verify(userService).getUser(email);
        verify(sessionService).getMovieSession(sessionId);
    }

    @Test
    void testCancelBooking() {
        // given
        String email = "user@mail.com";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        User user = getUser(1, email);

        MovieSessionSeat seat1 = getMovieSessionSeat(1, true, getHallSeat(1, 1, 1));
        MovieSessionSeat seat2 = getMovieSessionSeat(2, true, getHallSeat(1, 1, 2));

        long bookingId = 1;
        Booking booking = getBooking(bookingId);
        booking.setUser(user);
        booking.setSeats(Set.of(seat1, seat2));

        // when
        when(userService.getUser(email)).thenReturn(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        Booking result = bookingService.cancelBooking(authToken, bookingId);

        // then
        verify(userService).getUser(email);
        verify(bookingRepository).findById(bookingId);

        assertThat(result.getBookingStatus(), is(BookingStatus.CANCELED));
        assertThat(result.getSeats(), everyItem(hasProperty("booked", is(false))));
    }

    @Test
    void testCancelBookingThrowsExceptionIfUserIsInvalid() {
        // given
        String email = "user@mail.com";
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, "");
        User user = getUser(1, email);

        long bookingId = 1;
        Booking booking = getBooking(bookingId);
        booking.setUser(getUser(3, "invalid@mail.com"));

        // when
        when(userService.getUser(email)).thenReturn(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        // then
        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(authToken, bookingId));
        verify(userService).getUser(email);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetBooking() {
        // given
        long id = 2;
        Booking booking = getBooking(id);

        // when
        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));
        Booking result = bookingService.getBooking(id);

        // then
        verify(bookingRepository).findById(id);
        assertThat(result.getId(), is(booking.getId()));
    }


    @Test
    void testGetBookingThrowsExceptionIfBookingNotFound() {
        // given
        long id = 2;

        // when
        when(bookingRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(id));
        verify(bookingRepository).findById(id);
    }

    @Test
    void testGetBookingsByMovieSession() {
        // given
        long movieSessionId = 3;
        MovieSession movieSession = getMovieSession(movieSessionId, 15, null);

        List<Booking> bookings = List.of(
                getBooking(1),
                getBooking(2)
        );

        // when
        when(sessionService.getMovieSession(movieSessionId)).thenReturn(movieSession);
        when(bookingRepository.findByMovieSession(movieSession)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByMovieSession(movieSessionId);

        // then
        verify(sessionService).getMovieSession(movieSessionId);
        verify(bookingRepository).findByMovieSession(movieSession);

        assertThat(result, hasSize(bookings.size()));
    }

    @Test
    void testGetBookingsByUser() {
        // given
        long userId = 3;
        User user = getUser(userId, "");

        List<Booking> bookings = List.of(
                getBooking(1),
                getBooking(2)
        );

        // when
        when(userService.getUser(userId)).thenReturn(user);
        when(bookingRepository.findByUser(user)).thenReturn(bookings);

        List<Booking> result = bookingService.getBookingsByUser(userId);

        // then
        verify(userService).getUser(userId);
        verify(bookingRepository).findByUser(user);

        assertThat(result, hasSize(bookings.size()));
    }

    @Test
    void testGetAll() {
        // given
        List<Booking> bookings = List.of(
                getBooking(1),
                getBooking(2)
        );

        // when
        when(bookingRepository.findAll()).thenReturn(bookings);

        List<Booking> result = bookingService.getAll();

        // then
        verify(bookingRepository).findAll();
        assertThat(result, hasSize(bookings.size()));
    }

    private Booking getBooking(long id) {
        return Booking.builder()
                .id(id)
                .build();
    }

    private User getUser(long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .build();
    }

    private MovieSessionSeat getMovieSessionSeat(long id, boolean booked, MovieHallSeat hallSeat) {
        return MovieSessionSeat.builder()
                .id(id)
                .booked(booked)
                .hallSeat(hallSeat)
                .build();
    }

    private MovieHallSeat getHallSeat(long id, int row, int number) {
        return MovieHallSeat.builder()
                .id(id)
                .row(row)
                .number(number)
                .build();
    }

    private MovieSession getMovieSession(long id, double price, Set<MovieSessionSeat> seats) {
        return MovieSession.builder()
                .id(id)
                .price(price)
                .seats(seats)
                .build();
    }
}