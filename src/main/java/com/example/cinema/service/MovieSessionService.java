package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.MovieSessionDto;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.MovieSession;
import com.example.cinema.persistence.MovieHallRepository;
import com.example.cinema.persistence.MovieRepository;
import com.example.cinema.persistence.MovieSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieSessionService {
    private ModelMapper modelMapper;
    private MovieSessionRepository movieSessionRepository;
    private MovieRepository movieRepository;
    private MovieHallRepository movieHallRepository;

    public MovieSessionDto saveMovieSession(MovieSessionDto movieSessionDto) {
        log.debug("Save movieSession: {}", movieSessionDto);

        MovieSession movieSession = modelMapper.map(movieSessionDto, MovieSession.class);

        long movieId = movieSessionDto.getMovieId();
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        movieSession.setMovie(movie);

        LocalTime endsAt = movieSession.getStartsAt().plusMinutes(movie.getDuration());
        movieSessionDto.setEndsAt(endsAt);
        movieSession.setEndsAt(endsAt);

        long movieHallId = movieSessionDto.getMovieHallId();
        MovieHall movieHall = movieHallRepository.findById(movieHallId).orElseThrow(() -> new EntityNotFoundException(movieHallId, MovieHall.class));

        // check if hall is available at provided time
        boolean isMovieHallAvailable = checkMovieHallAvailability(movieHall, movieSessionDto);
        if (!isMovieHallAvailable) {
            throw new IllegalStateException("There is another movie at hall " + movieHallId + " at that time");
        }
        movieSession.setMovieHall(movieHall);

        movieSessionRepository.save(movieSession);
        return modelMapper.map(movieSession, MovieSessionDto.class);
    }

    @Transactional(readOnly = true)
    public MovieSessionDto getById(long id) {
        MovieSession movieSession = movieSessionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieSession.class));
        return modelMapper.map(movieSession, MovieSessionDto.class);
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getByMovie(long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findByMovie(movie).stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getAvailableByMovie(long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findAvailableByMovie(movie).stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getByDate(LocalDate date) {
        return movieSessionRepository.findByDate(date).stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getAvailableByDate(LocalDate date) {
        return movieSessionRepository.findAvailableByDate(date).stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getByMovieAndDate(long movieId, LocalDate date) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findByMovieAndDate(movie, date).stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getAvailableByMovieAndDate(long movieId, LocalDate date) {
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new EntityNotFoundException(movieId, Movie.class));

        return movieSessionRepository.findAvailableByMovieAndDate(movie, date).stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getAllAvailable() {
        return movieSessionRepository.findAllAvailable().stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieSessionDto> getAll() {
        return movieSessionRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, MovieSessionDto.class))
                .collect(Collectors.toList());
    }

    private boolean checkMovieHallAvailability(MovieHall movieHall, MovieSessionDto movieSessionDto) {
        List<MovieSession> movieSessionList = movieSessionRepository.findByMovieHallAndDate(movieHall, movieSessionDto.getDate());

        for (var movieSession : movieSessionList) {
            if (checkMovieBeginsDuringAnotherSession(movieSessionDto, movieSession) ||
                    checkMovieEndsDuringAnotherSession(movieSessionDto, movieSession)) {
                return false;
            }
        }

        return true;
    }

    private boolean checkMovieBeginsDuringAnotherSession(MovieSessionDto movieSessionDto, MovieSession movieSession) {
        return movieSessionDto.getStartsAt().isAfter(movieSession.getStartsAt()) &&
                movieSessionDto.getStartsAt().isBefore(movieSession.getEndsAt()) ||
                movieSessionDto.getStartsAt().equals(movieSession.getStartsAt()) ||
                movieSessionDto.getStartsAt().equals(movieSession.getEndsAt());
    }

    private boolean checkMovieEndsDuringAnotherSession(MovieSessionDto movieSessionDto, MovieSession movieSession) {
        return movieSessionDto.getEndsAt().isAfter(movieSession.getStartsAt()) &&
                movieSessionDto.getEndsAt().isBefore(movieSession.getEndsAt()) ||
                movieSessionDto.getEndsAt().equals(movieSession.getStartsAt()) ||
                movieSessionDto.getStartsAt().equals(movieSession.getEndsAt());
    }
}
