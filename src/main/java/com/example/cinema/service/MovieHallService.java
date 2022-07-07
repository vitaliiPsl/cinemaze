package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.MovieHallDto;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.persistence.MovieHallRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
@Transactional
public class MovieHallService {
    private final ModelMapper modelMapper;
    private final MovieHallRepository movieHallRepository;

    public MovieHallDto saveMovieHall(MovieHallDto movieHallDto){
        log.debug("save movie hall: {}", movieHallDto);

        MovieHall movieHall = modelMapper.map(movieHallDto, MovieHall.class);
        movieHallRepository.save(movieHall);

        return modelMapper.map(movieHall, MovieHallDto.class);
    }

    public MovieHallDto updateMovieHall(long id, MovieHallDto movieHallDto) {
        log.debug("update movie hall with id {}: {}", id, movieHallDto);
        movieHallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieHall.class));

        MovieHall movieHall = modelMapper.map(movieHallDto, MovieHall.class);
        movieHall.setId(id);

        movieHallRepository.save(movieHall);

        return modelMapper.map(movieHall, MovieHallDto.class);
    }

    @Transactional(readOnly = true)
    public MovieHallDto getMovieHall(long id){
        log.debug("get movie hall by id: {}", id);

        MovieHall movieHall = movieHallRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, MovieHall.class));

        return modelMapper.map(movieHall, MovieHallDto.class);
    }

    @Transactional(readOnly = true)
    public List<MovieHallDto> getAll(){
        log.debug("get all movie halls");

        return movieHallRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, MovieHallDto.class))
                .collect(Collectors.toList());
    }
}
