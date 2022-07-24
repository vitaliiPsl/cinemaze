package com.example.cinema.service;

import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.persistence.MovieRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {
    @Mock
    MovieRepository movieRepository;

    @Mock
    GenreService genreService;

    @Mock
    ImageService imageService;

    @InjectMocks
    MovieService movieService;

    @Test
    void testSaveMovie() {
        // given
        String movieName = "Test";
        Movie movie = getMovie(0, movieName);

        MultipartFile poster = new MockMultipartFile("poster", new byte[]{});
        MultipartFile[] previews = {
                new MockMultipartFile("preview1", new byte[]{}),
                new MockMultipartFile("preview2", new byte[]{}),
        };

        // when
        for (var preview : previews) {
            when(imageService.savePreviewImage(preview)).thenReturn(preview.getName());
        }
        when(imageService.savePosterImage(poster)).thenReturn(poster.getName());

        when(movieRepository.save(movie)).thenReturn(movie);
        Movie result = movieService.saveMovie(movie, poster, previews);

        // then
        verify(movieRepository).save(movie);
        verify(imageService).savePosterImage(poster);
        verify(imageService, times(previews.length)).savePreviewImage(ArgumentMatchers.any(MultipartFile.class));

        assertThat(result.getPosterImage(), notNullValue());
        assertThat(result.getPreviewImages(), hasSize(previews.length));
        assertThat(result.getName(), equalTo(movieName));
    }

    @Test
    void testSaveMovieDoesntSavePosterIfNull() {
        // given
        String movieName = "Test";
        Movie movie = getMovie(0, movieName);

        MultipartFile poster = null;
        MultipartFile[] previews = {};

        // when
        movieService.saveMovie(movie, poster, previews);

        // then
        verify(imageService, never()).savePosterImage(ArgumentMatchers.any(MultipartFile.class));
    }

    @Test
    void testSaveMovieDoesntSavePreviewsIfNull() {
        // given
        String movieName = "Test";
        Movie movie = getMovie(0, movieName);

        MultipartFile poster = new MockMultipartFile("poster", new byte[]{});
        MultipartFile[] previews = null;

        // when
        when(imageService.savePosterImage(poster)).thenReturn(poster.getName());
        when(movieRepository.save(movie)).thenReturn(movie);

        Movie result = movieService.saveMovie(movie, poster, previews);

        // then
        verify(movieRepository).save(movie);
        verify(imageService).savePosterImage(poster);
        verify(imageService, never()).savePreviewImage(ArgumentMatchers.any(MultipartFile.class));

        assertThat(result.getPosterImage(), notNullValue());
        assertThat(result.getPreviewImages(), hasSize(0));
        assertThat(result.getName(), equalTo(movieName));
    }

    @Test
    void testDeleteMovie() {
        // given
        long id = 1;
        String movieName = "Test";
        Movie movie = getMovie(id, movieName);

        // when
        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));

        movieService.deleteMovie(id);

        // then
        verify(movieRepository).findById(id);
        verify(movieRepository).delete(movie);
    }

    @Test
    void testUpdateMovie() {
        // given
        long id = 1;
        String movieName = "test";
        Movie movie = getMovie(0, movieName);

        // when
        when(movieRepository.findById(id)).thenReturn(Optional.of(getMovie(id, movieName)));
        when(movieRepository.save(movie)).thenReturn(movie);

        Movie result = movieService.updateMovie(id, movie);

        // then
        verify(movieRepository).findById(id);
        verify(movieRepository).save(movie);
        assertThat(result.getId(), equalTo(id));
        assertThat(result.getName(), is(movieName));
    }

    @Test
    void testUpdateMovieThrowsExceptionWhenMovieNotFound() {
        // given
        long id = 1;
        String movieName = "test";
        Movie movie = getMovie(id, movieName);

        // when
        when(movieRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> movieService.updateMovie(id, movie));
    }

    @Test
    void testGetMovieById() {
        // given
        long id = 1;
        String movieName = "test";
        Movie movie = getMovie(id, movieName);

        // when
        when(movieRepository.findById(id)).thenReturn(Optional.of(movie));
        Movie result = movieService.getMovie(id);

        // then
        verify(movieRepository).findById(id);
        assertThat(result.getName(), equalTo(movieName));
    }

    @Test
    void testGetMovieByIdThrowsExceptionWhenMovieNotFound() {
        // given
        long id = 1;

        // when
        when(movieRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> movieService.getMovie(id));
    }

    @Test
    void testGetMoviesByName() {
        // given
        String movieName = "test";
        List<Movie> movies = List.of(getMovie(1, "test"), getMovie(2, "test1"));

        // when
        when(movieRepository.findByNameContainingIgnoreCase(movieName)).thenReturn(movies);
        List<Movie> resultList = movieService.getMoviesByName(movieName);

        //then
        verify(movieRepository).findByNameContainingIgnoreCase(movieName);
        assertThat(resultList, hasSize(movies.size()));
    }

    @Test
    void testGetAllMovies() {
        // given
        List<Movie> movies = List.of(getMovie(1, "test"), getMovie(2, "test1"));

        // when
        when(movieRepository.findAll()).thenReturn(movies);
        List<Movie> resultList = movieService.getAllMovies();

        //then
        verify(movieRepository).findAll();
        assertThat(resultList, hasSize(movies.size()));
    }

    @Test
    void testAddGenreToMovie() {
        // given
        long movieId = 1;
        long genreId = 2;
        Movie movie = getMovie(movieId, "test");
        Genre genre = getGenre(genreId, "test");

        // when
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(genreService.getGenre(genreId)).thenReturn(genre);
        movieService.addGenreToMovie(movieId, genreId);

        // then
        verify(movieRepository).findById(movieId);
        verify(genreService).getGenre(genreId);
        assertThat(movie.getGenres(), Matchers.contains(genre));
    }

    @Test
    void testRemoveGenreFromMovie() {
        // given
        long movieId = 1;
        long genreId = 2;
        Genre genre = getGenre(genreId, "test");
        Movie movie = getMovie(movieId, "test");
        movie.getGenres().add(genre);

        // when
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(genreService.getGenre(genreId)).thenReturn(genre);
        movieService.removeGenreFromMovie(movieId, genreId);

        // then
        verify(movieRepository).findById(movieId);
        verify(genreService).getGenre(genreId);
        assertFalse(movie.getGenres().contains(genre));
    }

    private Movie getMovie(long id, String name) {
        return Movie.builder()
                .id(id)
                .name(name)
                .genres(new HashSet<>())
                .build();
    }

    private Genre getGenre(long id, String genreName) {
        return Genre.builder()
                .id(id)
                .genre(genreName)
                .build();
    }
}