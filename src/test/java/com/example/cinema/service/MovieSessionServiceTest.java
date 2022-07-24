package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.MovieSession;
import com.example.cinema.model.entities.movie.Seat;
import com.example.cinema.persistence.MovieSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieSessionServiceTest {

    @Mock
    MovieSessionRepository movieSessionRepository;

    @Mock
    MovieService movieService;

    @Mock
    MovieHallService movieHallService;

    @InjectMocks
    MovieSessionService movieSessionService;

    @Test
    void testSaveMovieSession() {
        // given
        long movieId = 1;
        long movieDuration = 123;
        Movie movie = getMovie(movieId, movieDuration);

        long movieHallId = 5;
        Set<Seat> hallSeats = Set.of(
                getSeat(1, 1, 1),
                getSeat(2, 1, 2)
        );
        MovieHall movieHall = getMovieHall(movieHallId, hallSeats);

        LocalDateTime startsAt = LocalDateTime.now().plusDays(5);
        MovieSession movieSession = getMovieSession(0, startsAt, null);

        List<MovieSession> otherSessions = List.of(
                getMovieSession(12, startsAt.minusMinutes(movieDuration).minusMinutes(10), startsAt.minusMinutes(10)),
                getMovieSession(13, startsAt.plusMinutes(movieDuration).plusMinutes(10), startsAt.plusMinutes(movieDuration).plusMinutes(100))
        );

        // when
        when(movieService.getMovie(movieId)).thenReturn(movie);
        when(movieHallService.getMovieHall(movieHallId)).thenReturn(movieHall);
        when(movieSessionRepository.findByMovieHallAndDate(movieHallId, startsAt.toLocalDate())).thenReturn(otherSessions);
        when(movieSessionRepository.save(movieSession)).thenReturn(movieSession);

        MovieSession result = movieSessionService.saveMovieSession(movieSession, movieId, movieHallId);

        // then
        verify(movieService).getMovie(movieId);
        verify(movieHallService).getMovieHall(movieHallId);
        verify(movieSessionRepository).findByMovieHallAndDate(movieHallId, startsAt.toLocalDate());
        verify(movieSessionRepository).save(movieSession);

        assertThat(result.getMovie(), is(movie));
        assertThat(result.getMovieHall(), is(movieHall));
        assertThat(result.getEndsAt(), is(startsAt.plusMinutes(movieDuration)));
        assertThat(result.getSeats(), hasSize(hallSeats.size()));
    }

    @Test
    void testSaveMovieSessionThrowsExceptionIfHallIsOccupied() {
        // given
        long movieId = 1;
        long movieDuration = 123;
        Movie movie = getMovie(movieId, movieDuration);

        long movieHallId = 5;
        MovieHall movieHall = getMovieHall(movieHallId, Set.of());

        LocalDateTime startsAt = LocalDateTime.now().plusDays(5);
        MovieSession movieSession = getMovieSession(0, startsAt, null);

        List<MovieSession> otherSessions = List.of(
                getMovieSession(3, startsAt.minusMinutes(10), startsAt.plusMinutes(movieDuration)),
                getMovieSession(4, startsAt.plusMinutes(movie.getDuration()).plusMinutes(30), startsAt.plusMinutes(movieDuration))
        );

        // when
        when(movieService.getMovie(movieId)).thenReturn(movie);
        when(movieHallService.getMovieHall(movieHallId)).thenReturn(movieHall);
        when(movieSessionRepository.findByMovieHallAndDate(movieHallId, startsAt.toLocalDate())).thenReturn(otherSessions);

        // then
        assertThrows(IllegalStateException.class, () -> movieSessionService.saveMovieSession(movieSession, movieId, movieHallId));

        verify(movieService).getMovie(movieId);
        verify(movieHallService).getMovieHall(movieHallId);
        verify(movieSessionRepository).findByMovieHallAndDate(movieHallId, startsAt.toLocalDate());
    }

    @Test
    void testGetById() {
        // given
        long id = 2;
        MovieSession movieSession = getMovieSession(id, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100));

        // when
        when(movieSessionRepository.findById(id)).thenReturn(Optional.of(movieSession));

        MovieSession result = movieSessionService.getMovieSession(id);

        // then
        verify(movieSessionRepository).findById(id);
        assertThat(result, is(movieSession));
    }

    @Test
    void testGetByIdThrowsExceptionIfMovieSessionNotFound() {
        // given
        long id = 2;

        // when
        when(movieSessionRepository.findById(id)).thenReturn(Optional.empty());


        // then
        assertThrows(EntityNotFoundException.class, () -> movieSessionService.getMovieSession(id));
        verify(movieSessionRepository).findById(id);
    }

    @Test
    void testGetByMovie() {
        // given
        long movieId = 2;
        Movie movie = getMovie(movieId, 123);
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieService.getMovie(movieId)).thenReturn(movie);
        when(movieSessionRepository.findByMovie(movie)).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getByMovie(movieId);

        // then
        verify(movieService).getMovie(movieId);
        verify(movieSessionRepository).findByMovie(movie);
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void testGetAvailableByMovie() {
        // given
        long movieId = 2;
        Movie movie = getMovie(movieId, 123);
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieService.getMovie(movieId)).thenReturn(movie);
        when(movieSessionRepository.findAvailableByMovie(movieId)).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getAvailableByMovie(movieId);

        // then
        verify(movieService).getMovie(movieId);
        verify(movieSessionRepository).findAvailableByMovie(movieId);
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void getByDate() {
        // given
        LocalDate date = LocalDate.now();
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.of(date, LocalTime.now()), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieSessionRepository.findByDate(date)).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getByDate(date);

        // then
        verify(movieSessionRepository).findByDate(date);
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void testGetAvailableByDate() {
        // given
        LocalDate date = LocalDate.now();
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.of(date, LocalTime.now()), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieSessionRepository.findAvailableByDate(date)).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getAvailableByDate(date);

        // then
        verify(movieSessionRepository).findAvailableByDate(date);
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void testGetByMovieAndDate() {
        // given
        long movieId = 2;
        Movie movie = getMovie(movieId, 123);

        LocalDate date = LocalDate.now();
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieService.getMovie(movieId)).thenReturn(movie);
        when(movieSessionRepository.findByMovieAndDate(movieId, date)).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getByMovieAndDate(movieId, date);

        // then
        verify(movieService).getMovie(movieId);
        verify(movieSessionRepository).findByMovieAndDate(movieId, date);
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void getAvailableByMovieAndDate() {
        // given
        long movieId = 2;
        Movie movie = getMovie(movieId, 123);

        LocalDate date = LocalDate.now();
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieService.getMovie(movieId)).thenReturn(movie);
        when(movieSessionRepository.findAvailableByMovieAndDate(movieId, date)).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getAvailableByMovieAndDate(movieId, date);

        // then
        verify(movieService).getMovie(movieId);
        verify(movieSessionRepository).findAvailableByMovieAndDate(movieId, date);
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void getAllAvailable() {
        // given
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieSessionRepository.findAllAvailable()).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getAllAvailable();

        // then
        verify(movieSessionRepository).findAllAvailable();
        assertThat(result, hasSize(movieSessions.size()));
    }

    @Test
    void getAll() {
        // given
        List<MovieSession> movieSessions = List.of(
                getMovieSession(0, LocalDateTime.now(), LocalDateTime.now().plusMinutes(100))
        );

        // when
        when(movieSessionRepository.findAll()).thenReturn(movieSessions);

        List<MovieSession> result = movieSessionService.getAll();

        // then
        verify(movieSessionRepository).findAll();
        assertThat(result, hasSize(movieSessions.size()));
    }

    private MovieSession getMovieSession(long id, LocalDateTime startsAt, LocalDateTime endsAt) {
        return MovieSession.builder()
                .id(id)
                .startsAt(startsAt)
                .endsAt(endsAt)
                .build();
    }

    private MovieHall getMovieHall(long id, Set<Seat> seats) {
        return MovieHall.builder()
                .id(id)
                .seats(seats)
                .build();
    }

    private Seat getSeat(long id, int row, int number) {
        return Seat.builder()
                .id(id)
                .row(row)
                .number(number)
                .build();
    }

    private Movie getMovie(long id, long duration) {
        return Movie.builder()
                .id(id)
                .duration(duration)
                .build();
    }
}