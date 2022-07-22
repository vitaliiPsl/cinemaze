package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.persistence.MovieHallRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieHallServiceTest {

    @Mock
    MovieHallRepository movieHallRepository;

    @InjectMocks
    MovieHallService movieHallService;

    @Test
    void tesSaveMovieHall() {
        // given
        int numberOfRows = 5;
        int numberOfSeatsPerRow = 10;
        MovieHall movieHall = getMovieHall(0, numberOfRows, numberOfSeatsPerRow);

        // when
        when(movieHallRepository.save(movieHall)).thenReturn(movieHall);
        MovieHall result = movieHallService.saveMovieHall(movieHall);

        // then
        verify(movieHallRepository).save(movieHall);

        assertThat(result, equalTo(movieHall));
        assertThat(result.getSeats(), hasSize(numberOfRows * numberOfSeatsPerRow));
    }

    @Test
    void testDeleteMovieHall() {
        // given
        long id = 3;
        MovieHall movieHall = getMovieHall(id, 3, 6);

        // when
        when(movieHallRepository.findById(id)).thenReturn(Optional.of(movieHall));
        movieHallService.deleteMovieHall(id);

        // then
        verify(movieHallRepository).findById(id);
        verify(movieHallRepository).delete(movieHall);
    }

    @Test
    void testDeleteMovieHallThrowExceptionIfDoesntExists() {
        // given
        long id = 3;

        // when
        when(movieHallRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> movieHallService.deleteMovieHall(id));

        verify(movieHallRepository).findById(id);
    }

    @Test
    void testGetMovieHall() {
        // given
        long id = 1;
        int rows = 8;
        int seats = 12;
        MovieHall movieHall = getMovieHall(id, rows, seats);

        // when
        when(movieHallRepository.findById(id)).thenReturn(Optional.of(movieHall));
        MovieHall result = movieHallService.getMovieHall(id);

        // then
        verify(movieHallRepository).findById(id);

        assertThat(result, equalTo(movieHall));
    }

    @Test
    void testGetMovieHallThrowsExceptionWhenMovieHallNotFound() {
        // given
        long id = 1;

        // when
        when(movieHallRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> movieHallService.getMovieHall(id));
    }

    @Test
    void testGetAll() {
        // given
        List<MovieHall> movieHallList = List.of(
                getMovieHall(1, 5, 12),
                getMovieHall(2, 6, 10)
        );

        // when
        when(movieHallRepository.findAll()).thenReturn(movieHallList);
        List<MovieHall> result = movieHallService.getAll();

        // then
        verify(movieHallRepository).findAll();
        assertThat(result, hasSize(movieHallList.size()));
    }

    private MovieHall getMovieHall(long id, int rows, int seats) {
        return MovieHall.builder()
                .id(id)
                .numberOfSeatRows(rows)
                .numberOfSeatsPerRow(seats)
                .seats(new HashSet<>())
                .build();
    }
}