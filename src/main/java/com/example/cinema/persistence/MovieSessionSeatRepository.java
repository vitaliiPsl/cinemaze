package com.example.cinema.persistence;

import com.example.cinema.model.entities.movie.MovieSessionSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieSessionSeatRepository extends JpaRepository<MovieSessionSeat, Long> {

}
