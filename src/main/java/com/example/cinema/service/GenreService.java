package com.example.cinema.service;

import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.persistence.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public void saveGenre(Genre genre){
        genreRepository.save(genre);
    }

    public void deleteGenre(long id){
        genreRepository.findById(id).ifPresent(genreRepository::delete);
    }

    public Optional<Genre> getGenre(long id){
        return genreRepository.findById(id);
    }

    public Optional<Genre> getGenre(String genre){
        return genreRepository.findByGenre(genre);
    }

    public List<Genre> getAllGenres(){
        return genreRepository.findAll();
    }
}
