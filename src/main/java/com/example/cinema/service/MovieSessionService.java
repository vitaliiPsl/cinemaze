package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.*;
import com.example.cinema.model.entities.session.MovieHall;
import com.example.cinema.model.entities.session.MovieHallSeat;
import com.example.cinema.model.entities.session.MovieSession;
import com.example.cinema.model.entities.session.MovieSessionSeat;
import com.example.cinema.persistence.MovieSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieSessionService {
    private MovieSessionRepository movieSessionRepository;
    private MovieService movieService;
    private MovieHallService movieHallService;

    public MovieSession saveMovieSession(MovieSession movieSession, long movieId, long movieHallId) {
        log.debug("Save movieSession: {}", movieSession);

        Movie movie = movieService.getMovie(movieId);
        movieSession.setMovie(movie);

        LocalDateTime endsAt = movieSession.getStartsAt().plusMinutes(movie.getDuration());
        movieSession.setEndsAt(endsAt);

        MovieHall movieHall = movieHallService.getMovieHall(movieHallId);

        // check if hall is available at provided time
        boolean isMovieHallAvailable = checkMovieHallAvailability(movieHall, movieSession.getStartsAt(), movieSession.getEndsAt());
        if (!isMovieHallAvailable) {
            throw new IllegalStateException("There is another movie at hall " + movieHallId + " at that time");
        }
        movieSession.setMovieHall(movieHall);

        Set<MovieSessionSeat> sessionSeats = getSessionSeats(movieSession, movieHall);
        movieSession.setSeats(sessionSeats);

        return movieSessionRepository.save(movieSession);
    }

    @Transactional(readOnly = true)
    public MovieSession getMovieSession(long id) {
        return movieSessionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieSession.class));
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getByMovie(long movieId) {
        Movie movie = movieService.getMovie(movieId);

        return movieSessionRepository.findByMovie(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAvailableByMovie(long movieId) {
        Movie movie = movieService.getMovie(movieId);

        return movieSessionRepository.findAvailableByMovie(movie.getId());
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getByDate(LocalDate date) {
        return movieSessionRepository.findByDate(date);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAvailableByDate(LocalDate date) {
        return movieSessionRepository.findAvailableByDate(date);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getByMovieAndDate(long movieId, LocalDate date) {
        Movie movie = movieService.getMovie(movieId);

        return movieSessionRepository.findByMovieAndDate(movie.getId(), date);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAvailableByMovieAndDate(long movieId, LocalDate date) {
        Movie movie = movieService.getMovie(movieId);

        return movieSessionRepository.findAvailableByMovieAndDate(movie.getId(), date);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAllAvailable() {
        return movieSessionRepository.findAllAvailable();
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAll() {
        return movieSessionRepository.findAll();
    }

    private Set<MovieSessionSeat> getSessionSeats(MovieSession session, MovieHall hall) {
        Set<MovieSessionSeat> sessionSeats = new HashSet<>();

        for (MovieHallSeat hallSeat : hall.getSeats()) {
            MovieSessionSeat sessionSeat = new MovieSessionSeat(session, hallSeat);
            sessionSeats.add(sessionSeat);
        }

        return sessionSeats;
    }

    private boolean checkMovieHallAvailability(MovieHall movieHall, LocalDateTime startsAt, LocalDateTime endsAt) {
        List<MovieSession> movieSessionList = movieSessionRepository.findByMovieHallAndDate(movieHall.getId(), startsAt.toLocalDate());

        for (var movieSession : movieSessionList) {
            if (checkStartsDuringAnotherSession(startsAt, movieSession) || checkEndsDuringAnotherSession(endsAt, movieSession)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkStartsDuringAnotherSession(LocalDateTime startsAt, MovieSession movieSession) {
        return !startsAt.isBefore(movieSession.getStartsAt()) && !startsAt.isAfter(movieSession.getEndsAt());
    }

    private boolean checkEndsDuringAnotherSession(LocalDateTime endsAt, MovieSession movieSession) {
        return !endsAt.isBefore(movieSession.getStartsAt()) && !endsAt.isAfter(movieSession.getEndsAt());
    }
}
