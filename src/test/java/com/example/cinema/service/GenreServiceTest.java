package com.example.cinema.service;

import com.example.cinema.exceptions.EntityAlreadyExistsException;
import com.example.cinema.exceptions.EntityNotFoundException;
import com.example.cinema.model.dto.GenreDto;
import com.example.cinema.model.entities.movie.Genre;
import com.example.cinema.persistence.GenreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    ModelMapper modelMapper;

    @Mock
    GenreRepository genreRepository;

    @InjectMocks
    GenreService genreService;

    @Test
    void testSaveGenre() {
        // given
        String genreName = "test";
        Genre genre = getGenre(0, genreName);
        GenreDto genreDto = getGenreDto(genreName);

        // when
        when(genreRepository.findByGenre(genreDto.getGenre())).thenReturn(Optional.empty());
        when(modelMapper.map(genreDto, Genre.class)).thenReturn(genre);
        when(modelMapper.map(genre, GenreDto.class)).thenReturn(getGenreDto(genreName));
        GenreDto result = genreService.saveGenre(genreDto);

        // then
        assertThat(result.getGenre(), equalTo(genreName));
        verify(genreRepository).findByGenre(genreDto.getGenre());
        verify(modelMapper).map(genreDto, Genre.class);
    }

    @Test
    void testSaveGenreThrowsExceptionIfGenreAlreadyExists() {
        // given
        String genreName = "test";
        Genre genre = getGenre(0, genreName);
        GenreDto genreDto = getGenreDto(genreName);

        // when
        when(genreRepository.findByGenre(genreDto.getGenre())).thenReturn(Optional.of(genre));

        // then
        assertThrows(EntityAlreadyExistsException.class, () -> genreService.saveGenre(genreDto));
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
        GenreDto genreDto = getGenreDto(genreName);
        Genre genre = getGenre(0, genreName);

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.of(getGenre(id, "")));
        when(modelMapper.map(genreDto, Genre.class)).thenReturn(genre);
        genreService.updateGenre(id, genreDto);

        // then
        assertThat(genre.getId(), equalTo(id));
        verify(genreRepository).findById(id);
        verify(genreRepository).save(genre);
    }

    @Test
    void testUpdateGenreThrowsExceptionWhenGenreNotFound() {
        // given
        long id = 1;
        String genreName = "test";
        GenreDto genreDto = getGenreDto(genreName);

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(EntityNotFoundException.class, () -> genreService.updateGenre(id, genreDto));
    }

    @Test
    void testGetGenreById() {
        // given
        long id = 1;
        String genreName = "test";
        Genre genre = getGenre(id, genreName);
        GenreDto genreDto = getGenreDto(genreName);

        // when
        when(genreRepository.findById(id)).thenReturn(Optional.of(genre));
        when(modelMapper.map(genre, GenreDto.class)).thenReturn(genreDto);
        GenreDto result = genreService.getGenre(id);

        // then
        verify(genreRepository).findById(id);
        verify(modelMapper).map(genre, GenreDto.class);
        assertThat(result.getGenre(), equalTo(genreDto.getGenre()));
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
        GenreDto genreDto = getGenreDto(genreName);

        // when
        when(genreRepository.findByGenre(genreName)).thenReturn(Optional.of(genre));
        when(modelMapper.map(genre, GenreDto.class)).thenReturn(genreDto);
        GenreDto result = genreService.getGenre(genreName);

        // then
        verify(genreRepository).findByGenre(genreName);
        verify(modelMapper).map(genre, GenreDto.class);
        assertThat(result.getGenre(), equalTo(genreDto.getGenre()));
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
        for (var genre : genreList) {
            when(modelMapper.map(genre, GenreDto.class)).thenReturn(getGenreDto(genre.getGenre()));
        }
        List<GenreDto> genreDtoList = genreService.getAllGenres();

        // then
        verify(genreRepository).findAll();
        verify(modelMapper, times(genreList.size())).map(any(Genre.class), eq(GenreDto.class));
        assertThat(genreDtoList, hasSize(genreList.size()));
    }

    @Test
    void testMapGenreToGenreDto() {
        // given
        String genreName = "test";
        Genre genre = getGenre(0, genreName);
        GenreDto genreDto = getGenreDto(genreName);

        // when
        when(modelMapper.map(genre, GenreDto.class)).thenReturn(genreDto);
        GenreDto result = genreService.mapGenreToGenreDto(genre);

        // then
        verify(modelMapper).map(genre, GenreDto.class);
        assertThat(result.getGenre(), equalTo(genreDto.getGenre()));
    }

    private Genre getGenre(long id, String genreName) {
        Genre genre = new Genre();
        genre.setId(id);
        genre.setGenre(genreName);

        return genre;
    }

    private GenreDto getGenreDto(String genreName) {
        GenreDto genreDto = new GenreDto();
        genreDto.setGenre(genreName);

        return genreDto;
    }
}