package com.example.cinema.web;

import com.example.cinema.model.dto.GenreDto;
import com.example.cinema.service.GenreService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public GenreDto saveGenre(@Valid @RequestBody GenreDto genre){
        return genreService.saveGenre(genre);
    }

    @DeleteMapping("/{id}")
    public void deleteGenre(@PathVariable(name = "id") long id){
        genreService.deleteGenre(id);
    }

    @GetMapping
    public List<GenreDto> getAllGenres(){
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public GenreDto getGenre(@PathVariable(name = "id") long id){
        return genreService.getGenre(id);
    }
}
