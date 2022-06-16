package com.example.cinema.service;

import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.persistence.ImageRepository;
import com.example.cinema.persistence.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovieService {
    private final MovieRepository movieRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository, ImageRepository imageRepository) {
        this.movieRepository = movieRepository;
        this.imageRepository = imageRepository;
    }

    public void saveMovie(Movie movie, MultipartFile posterImage, MultipartFile[] previewImages){
        String poster = imageRepository.savePosterImage(posterImage);
        movie.setPosterImage(poster);

        for (MultipartFile previewImage : previewImages) {
            String preview = imageRepository.savePreviewImage(previewImage);
            movie.addPreviewImage(preview);
        }

        movieRepository.save(movie);
    }

    public void updateMovie(Movie movie){
        movieRepository.save(movie);
    }

    public void deleteMovie(long id){
        getMovie(id).ifPresent(movieRepository::delete);
    }

    public Optional<Movie> getMovie(long id){
        return movieRepository.findById(id);
    }

    public byte[] getPosterImage(String identifier){
        return imageRepository.loadPosterImage(identifier);
    }

    public byte[] getPreviewImage(String identifier){
        return imageRepository.loadPreviewImage(identifier);
    }

    public List<Movie> getMoviesByName(String name){
        return movieRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Movie> getAllMovies(){
        return movieRepository.findAll();
    }
}
