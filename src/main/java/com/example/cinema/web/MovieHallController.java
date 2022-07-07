package com.example.cinema.web;

import com.example.cinema.model.dto.MovieHallDto;
import com.example.cinema.service.MovieHallService;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/movie-halls")
public class MovieHallController {
    private final MovieHallService movieHallService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieHallDto saveMovieHall(@Valid @RequestBody MovieHallDto movieHallDto){
        return movieHallService.saveMovieHall(movieHallDto);
    }

    @PutMapping("{id}")
    public MovieHallDto updateMovieHall(@PathVariable long id, @Valid @RequestBody MovieHallDto movieHallDto){
        return movieHallService.updateMovieHall(id, movieHallDto);
    }

    @GetMapping
    public List<MovieHallDto> getAll(){
        return movieHallService.getAll();
    }

    @GetMapping("{id}")
    public MovieHallDto getHallById(@PathVariable long id){
        return movieHallService.getMovieHall(id);
    }
}
