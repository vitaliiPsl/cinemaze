package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.Seat;
import com.example.cinema.persistence.MovieHallRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieHallService {
    private final MovieHallRepository movieHallRepository;

    public MovieHall saveMovieHall(MovieHall movieHall) {
        log.debug("save movie hall: {}", movieHall);

        Set<Seat> seats = getSeats(movieHall);
        movieHall.setSeats(seats);

        return movieHallRepository.save(movieHall);
    }

    public void deleteMovieHall(long id) {
        MovieHall movieHall = movieHallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieHall.class));

        movieHallRepository.delete(movieHall);
    }

    @Transactional(readOnly = true)
    public MovieHall getMovieHall(long id) {
        log.debug("get movie hall by id: {}", id);

        return movieHallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieHall.class));
    }

    @Transactional(readOnly = true)
    public List<MovieHall> getAll() {
        log.debug("get all movie halls");

        return movieHallRepository.findAll();
    }

    private Set<Seat> getSeats(MovieHall movieHall) {
        Set<Seat> seats = new HashSet<>();

        for (int row = 0; row < movieHall.getNumberOfSeatRows(); row++) {
            for (int seatNumber = 0; seatNumber < movieHall.getNumberOfSeatsPerRow(); seatNumber++) {
                Seat seat = new Seat();
                seat.setRow(row);
                seat.setNumber(seatNumber);
                seat.setMovieHall(movieHall);

                seats.add(seat);
            }
        }

        return seats;
    }
}
