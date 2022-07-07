package com.example.cinema.persistence;

import com.example.cinema.model.entities.movie.MovieHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieHallRepository extends JpaRepository<MovieHall, Long> {
}
