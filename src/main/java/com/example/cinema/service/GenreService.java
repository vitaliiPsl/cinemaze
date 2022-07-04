package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.GenreDto;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.persistence.GenreRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class GenreService {
    public final ModelMapper modelMapper;
    private final GenreRepository genreRepository;

    public GenreDto saveGenre(GenreDto genreDto){
        Optional<Genre> possibleGenre = genreRepository.findByGenre(genreDto.getGenre());
        if(possibleGenre.isPresent()){
            throw new EntityAlreadyExistsException(genreDto.getGenre(), Genre.class);
        }

        Genre genre = modelMapper.map(genreDto, Genre.class);
        genreRepository.save(genre);
        return modelMapper.map(genre, GenreDto.class);
    }

    public void deleteGenre(long id){
        Genre genre = genreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Genre.class));
        genreRepository.delete(genre);
    }

    public void updateGenre(long id, GenreDto genreDto){
        genreRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id, Movie.class));

        Genre genre = modelMapper.map(genreDto, Genre.class);
        genre.setId(id);

        genreRepository.save(genre);
    }

    @Transactional(readOnly = true)
    public GenreDto getGenre(long id){
        return genreRepository.findById(id).map(this::mapGenreToGenreDto)
                .orElseThrow(() -> new EntityNotFoundException(id, Genre.class));
    }

    @Transactional(readOnly = true)
    public GenreDto getGenre(String genre){
        return genreRepository.findByGenre(genre).map(this::mapGenreToGenreDto)
                .orElseThrow(() -> new EntityNotFoundException(genre, Genre.class));
    }

    @Transactional(readOnly = true)
    public List<GenreDto> getAllGenres(){
        return genreRepository.findAll()
                .stream().map(this::mapGenreToGenreDto)
                .collect(Collectors.toList());
    }

    public GenreDto mapGenreToGenreDto(Genre genre) {
        return modelMapper.map(genre, GenreDto.class);
    }
}
