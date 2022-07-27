package com.example.cinema.persistence;

import com.example.cinema.model.entities.movie.Movie;
import com.example.cinema.model.entities.session.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovieSessionRepository extends JpaRepository<MovieSession, Long> {
    List<MovieSession> findByMovie(Movie movie);

    @Query(value = "select * from movie_session ms where DATE(ms.starts_at) = :date", nativeQuery = true)
    List<MovieSession> findByDate(LocalDate date);

    @Query(value = "select * from movie_session ms where ms.movie_id = :movieId and DATE(ms.starts_at) >= CURRENT_DATE", nativeQuery = true)
    List<MovieSession> findAvailableByMovie(long movieId);

    @Query(value = "select * from movie_session ms where DATE(ms.starts_at) = :date and (ms.starts_at) >= CURRENT_TIMESTAMP", nativeQuery = true)
    List<MovieSession> findAvailableByDate(LocalDate date);

    @Query(value = "select * from movie_session ms where ms.movie_id = :movieId and DATE(ms.starts_at) = :date", nativeQuery = true)
    List<MovieSession> findByMovieAndDate(long movieId, LocalDate date);

    @Query(value = "select * from movie_session ms where ms.movie_id = :movieId and DATE(ms.starts_at) = :date and ms.starts_at >= CURRENT_TIMESTAMP", nativeQuery = true)
    List<MovieSession> findAvailableByMovieAndDate(long movieId, LocalDate date);

    @Query(value = "select * from movie_session ms where ms.movie_hall_id = :movieHallId and DATE(ms.starts_at) = :date", nativeQuery = true)
    List<MovieSession> findByMovieHallAndDate(long movieHallId, LocalDate date);

    @Query(value = "select * from movie_session ms where ms.starts_at >= CURRENT_TIMESTAMP", nativeQuery = true)
    List<MovieSession> findAllAvailable();
}
