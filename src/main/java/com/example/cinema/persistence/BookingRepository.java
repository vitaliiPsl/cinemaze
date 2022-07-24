package com.example.cinema.persistence;

import com.example.cinema.model.entities.booking.Booking;
import com.example.cinema.model.entities.User;
import com.example.cinema.model.entities.movie.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByMovieSession(MovieSession session);
}
