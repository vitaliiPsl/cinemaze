package com.example.cinema.web;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.service.GenreService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/genres")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public void saveGenre(@Valid @RequestBody Genre genre){
        Optional<Genre> existing = genreService.getGenre(genre.getGenre());

        if(existing.isPresent()){
            throw new EntityAlreadyExistsException(genre.getGenre(), Genre.class);
        }

        genreService.saveGenre(genre);
    }

    @DeleteMapping("/{id}")
    public void deleteGenre(@PathVariable(name = "id") long id){
        genreService.deleteGenre(id);
    }

    @GetMapping
    public List<Genre> getAllGenres(){
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable(name = "id") long id){
        return genreService.getGenre(id).orElseThrow(() -> new EntityNotFoundException(id, Genre.class));
    }
}
