package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.MovieHallDto;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.persistence.MovieHallRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieHallServiceTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    MovieHallRepository movieHallRepository;

    @InjectMocks
    MovieHallService movieHallService;

    @Test
    void tesSaveMovieHall() {
        // given
        int numberOfRows = 5;
        int numberOfSeatsPerRow = 10;
        MovieHallDto movieHallDto = getMovieHallDto(numberOfRows, numberOfSeatsPerRow);
        MovieHall movieHall = getMovieHall(numberOfRows, numberOfSeatsPerRow);

        // when
        when(modelMapper.map(movieHallDto, MovieHall.class)).thenReturn(movieHall);
        when(modelMapper.map(movieHall, MovieHallDto.class)).thenReturn(movieHallDto);
        MovieHallDto result = movieHallService.saveMovieHall(movieHallDto);

        // then
        verify(modelMapper).map(movieHallDto, MovieHall.class);
        verify(movieHallRepository).save(movieHall);
        verify(modelMapper).map(movieHall, MovieHallDto.class);

        assertThat(movieHall.getSeats(), hasSize(numberOfRows * numberOfSeatsPerRow));
        assertThat(result, equalTo(movieHallDto));
    }

    @Test
    void testGetMovieHall() {
        // given
        long id = 1;
        int rows = 8;
        int seats = 12;
        MovieHall movieHall = getMovieHall(rows, seats);
        MovieHallDto movieHallDto = getMovieHallDto(rows, seats);

        // when
        when(movieHallRepository.findById(id)).thenReturn(Optional.of(movieHall));
        when(modelMapper.map(movieHall, MovieHallDto.class)).thenReturn(movieHallDto);
        MovieHallDto result = movieHallService.getMovieHall(id);

        // then
        verify(movieHallRepository).findById(id);
        verify(modelMapper).map(movieHall, MovieHallDto.class);
        assertThat(result, equalTo(movieHallDto));
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
                getMovieHall(5, 12),
                getMovieHall(6, 10)
        );

        // when
        for (MovieHall movieHall : movieHallList) {
            when(modelMapper.map(movieHall, MovieHallDto.class))
                    .thenReturn(getMovieHallDto(movieHall.getNumberOfSeatRows(), movieHall.getNumberOfSeatsPerRow()));
        }

        when(movieHallRepository.findAll()).thenReturn(movieHallList);
        List<MovieHallDto> result = movieHallService.getAll();

        // then
        verify(movieHallRepository).findAll();
        verify(modelMapper, times(movieHallList.size())).map(any(MovieHall.class), eq(MovieHallDto.class));
        assertThat(result, hasSize(movieHallList.size()));
    }

    private MovieHall getMovieHall(int rows, int seats){
        MovieHall movieHall = new MovieHall();
        movieHall.setNumberOfSeatRows(rows);
        movieHall.setNumberOfSeatsPerRow(seats);

        return movieHall;
    }

    private MovieHallDto getMovieHallDto(int rows, int seats){
        MovieHallDto movieHallDto = new MovieHallDto();
        movieHallDto.setNumberOfSeatRows(rows);
        movieHallDto.setNumberOfSeatsPerRow(seats);

        return movieHallDto;
    }
}