package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.MovieHallDto;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.Seat;
import com.example.cinema.persistence.MovieHallRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieHallService {
    private final ModelMapper modelMapper;
    private final MovieHallRepository movieHallRepository;

    public MovieHallDto saveMovieHall(MovieHallDto movieHallDto) {
        log.debug("save movie hall: {}", movieHallDto);

        MovieHall movieHall = modelMapper.map(movieHallDto, MovieHall.class);

        Set<Seat> seats = getSeats(movieHall);
        movieHall.setSeats(seats);

        movieHallRepository.save(movieHall);

        return modelMapper.map(movieHall, MovieHallDto.class);
    }

    @Transactional(readOnly = true)
    public MovieHallDto getMovieHall(long id) {
        log.debug("get movie hall by id: {}", id);

        MovieHall movieHall = movieHallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieHall.class));

        return modelMapper.map(movieHall, MovieHallDto.class);
    }

    @Transactional(readOnly = true)
    public List<MovieHallDto> getAll() {
        log.debug("get all movie halls");

        return movieHallRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, MovieHallDto.class))
                .collect(Collectors.toList());
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
