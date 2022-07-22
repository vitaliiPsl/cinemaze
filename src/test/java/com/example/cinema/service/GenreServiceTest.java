package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    GenreRepository genreRepository;

    @InjectMocks
    GenreService genreService;

    @Test
    void testSaveGenre() {
        // given
        String genreName = "test";
        Genre genre = getGenre(0, genreName);

        // when
        when(genreRepository.findByGenre(genre.getGenre())).thenReturn(Optional.empty());
        when(genreRepository.save(genre)).thenReturn(genre);

        Genre result = genreService.saveGenre(genre);

        // then
        verify(genreRepository).findByGenre(genre.getGenre());
        verify(genreRepository).save(genre);

        assertThat(result.getGenre(), equalTo(genreName));
    }

    @Test
    void testSaveGenreThrowsExceptionIfGenreAlreadyExists() {
        // given
        String genreName = "test";
        Genre genre = getGenre(0, genreName);

        // when
        when(genreRepository.findByGenre(genre.getGenre())).thenReturn(Optional.of(genre));

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> genreService.saveGenre(genre));
    }

    @Test
    void testDeleteGenre() {
        // given
        long id = 1;
        Genre genre = getGenre(id, "");

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
        genreService.deleteGenre(id);

        // then
        verify(genreRepository).findById(id);
        verify(genreRepository).delete(genre);
    }

    @Test
    void testDeleteGenreThrowsExceptionIfGenreNotFound() {
        // given
        long id = 1;
        Genre genre = getGenre(id, "");

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> genreService.deleteGenre(id));
    }

    @Test
    void testUpdateGenre() {
        // given
        long id = 1;
        String genreName = "test";
        Genre genre = getGenre(0, genreName);

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.of(getGenre(id, "")));
        when(genreRepository.save(genre)).thenReturn(genre);

        Genre result = genreService.updateGenre(id, genre);

        // then
        verify(genreRepository).findById(id);
        verify(genreRepository).save(genre);

        assertThat(result.getId(), is(id));
        assertThat(result.getGenre(), is(genreName));
    }

    @Test
    void testUpdateGenreThrowsExceptionWhenGenreNotFound() {
        // given
        long id = 1;
        String genreName = "test";
        Genre genre = getGenre(0, genreName);

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> genreService.updateGenre(id, genre));
    }

    @Test
    void testGetGenreById() {
        // given
        long id = 1;
        String genreName = "test";
        Genre genre = getGenre(id, genreName);

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
        Genre result = genreService.getGenre(id);

        // then
        verify(genreRepository).findById(id);
        assertThat(result.getGenre(), is(genre.getGenre()));
    }

    @Test
    void testGetGenreByIdThrowsExceptionWhenGenreNotFound() {
        // given
        long id = 1;

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> genreService.getGenre(id));
    }

    @Test
    void testGetGenreByGenreName() {
        // given
        long id = 1;
        String genreName = "test";
        Genre genre = getGenre(id, genreName);

        // when
        when(genreRepository.findByGenre(genreName)).thenReturn(Optional.of(genre));
        Genre result = genreService.getGenre(genreName);

        // then
        verify(genreRepository).findByGenre(genreName);
        assertThat(result.getGenre(), equalTo(genre.getGenre()));
    }

    @Test
    void testGetGenreByGenreNameThrowsExceptionWhenGenreNotFound() {
        // given
        String genreName = "test";

        // when
        when(genreRepository.findByGenre(genreName)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> genreService.getGenre(genreName));
    }

    @Test
    void testGetAllGenres() {
        // given
        List<Genre> genreList = List.of(getGenre(1, "test"), getGenre(2, "test1"), getGenre(3, "test3"));

        // when
        when(genreRepository.findAll()).thenReturn(genreList);
        List<Genre> resultList = genreService.getAllGenres();

        // then
        verify(genreRepository).findAll();
        assertThat(resultList, hasSize(genreList.size()));
    }

    private Genre getGenre(long id, String genreName) {
        return Genre.builder()
                .id(id)
                .genre(genreName)
                .build();
    }
}