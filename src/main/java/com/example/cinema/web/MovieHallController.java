package com.example.cinema.web;

import com.example.cinema.model.dto.MovieHallDto;
import com.example.cinema.model.entities.session.MovieHall;
import com.example.cinema.service.MovieHallService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("api/movie-halls")
public class MovieHallController {
    private final ModelMapper modelMapper;
    private final MovieHallService movieHallService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieHallDto saveMovieHall(@Valid @RequestBody MovieHallDto movieHallDto) {
        MovieHall movieHall = modelMapper.map(movieHallDto, MovieHall.class);
        movieHall = movieHallService.saveMovieHall(movieHall);

        return mapMovieHallToMovieHallDto(movieHall);
    }

    @DeleteMapping("{id}")
    public void deleteMovieHall(@PathVariable long id) {
        movieHallService.deleteMovieHall(id);
    }

    @GetMapping("{id}")
    public MovieHallDto getHallById(@PathVariable long id) {
        MovieHall movieHall = movieHallService.getMovieHall(id);

        return mapMovieHallToMovieHallDto(movieHall);
    }

    @GetMapping
    public List<MovieHallDto> getAll() {
        List<MovieHall> movieHalls = movieHallService.getAll();

        return movieHalls.stream().map(this::mapMovieHallToMovieHallDto).collect(Collectors.toList());
    }

    private MovieHallDto mapMovieHallToMovieHallDto(MovieHall movieHall) {
        return modelMapper.map(movieHall, MovieHallDto.class);
    }
}
