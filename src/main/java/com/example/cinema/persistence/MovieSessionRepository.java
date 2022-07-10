package com.example.cinema.persistence;

import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.movie.MovieHall;
import com.example.cinema.model.entities.movie.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {
    List<MovieSession> findByMovie(Movie movie);

    @Query("select ms from MovieSession ms where ms.movie = ?1 and ms.date >= CURRENT_DATE()")
    List<MovieSession> findAvailableByMovie(Movie movie);

    List<MovieSession> findByDate(LocalDate date);

    @Query("select ms from MovieSession ms where ms.date = ?1 and ms.date >= CURRENT_DATE()")
    List<MovieSession> findAvailableByDate(LocalDate date);

    List<MovieSession> findByMovieAndDate(Movie movie, LocalDate date);

    @Query("select ms from MovieSession ms where ms.movie = ?1 and ms.date = ?2 and ms.date >= CURRENT_DATE()")
    List<MovieSession> findAvailableByMovieAndDate(Movie movie, LocalDate date);

    List<MovieSession> findByMovieHallAndDate(MovieHall movieHall, LocalDate date);

    @Query("select ms from MovieSession ms where ms.date >= CURRENT_DATE()")
    List<MovieSession> findAllAvailable();
}
