package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.MovieSession;
import com.example.cinema.persistence.MovieHallRepository;
import com.example.cinema.persistence.MovieRepository;
import com.example.cinema.persistence.MovieSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieSessionService {
    private MovieSessionRepository movieSessionRepository;
    private MovieRepository movieRepository;
    private MovieHallRepository movieHallRepository;

    public MovieSession saveMovieSession(MovieSession movieSession, long movieId, long movieHallId) {
        log.debug("Save movieSession: {}", movieSession);

        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        movieSession.setMovie(movie);

        LocalTime endsAt = movieSession.getStartsAt().plusMinutes(movie.getDuration());
        movieSession.setEndsAt(endsAt);

        MovieHall movieHall = movieHallRepository.findById(movieHallId).orElseThrow(() -> new EntityNotFoundException(movieHallId, MovieHall.class));

        // check if hall is available at provided time
        boolean isMovieHallAvailable =
                checkMovieHallAvailability(movieHall, movieSession.getDate(), movieSession.getStartsAt(), endsAt);
        if (!isMovieHallAvailable) {
            throw new IllegalStateException("There is another movie at hall " + movieHallId + " at that time");
        }
        movieSession.setMovieHall(movieHall);

        return movieSessionRepository.save(movieSession);
    }

    @Transactional(readOnly = true)
    public MovieSession getById(long id) {
        return movieSessionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieSession.class));
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getByMovie(long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findByMovie(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAvailableByMovie(long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findAvailableByMovie(movie);
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
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findByMovieAndDate(movie, date);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAvailableByMovieAndDate(long movieId, LocalDate date) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findAvailableByMovieAndDate(movie, date);
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAllAvailable() {
        return movieSessionRepository.findAllAvailable();
    }

    @Transactional(readOnly = true)
    public List<MovieSession> getAll() {
        return movieSessionRepository.findAll();
    }

    private boolean checkMovieHallAvailability(MovieHall movieHall, LocalDate date, LocalTime startsAt, LocalTime endsAt) {
        List<MovieSession> movieSessionList = movieSessionRepository.findByMovieHallAndDate(movieHall, date);

        for (var movieSession : movieSessionList) {
            if (checkStartsDuringAnotherSession(startsAt, movieSession) || checkEndsDuringAnotherSession(endsAt, movieSession)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkStartsDuringAnotherSession(LocalTime startsAt, MovieSession movieSession) {
        return !startsAt.isBefore(movieSession.getStartsAt()) && !startsAt.isAfter(movieSession.getEndsAt());
    }

    private boolean checkEndsDuringAnotherSession(LocalTime endsAt, MovieSession movieSession) {
        return !endsAt.isBefore(movieSession.getStartsAt()) && !endsAt.isAfter(movieSession.getEndsAt());
    }
}
