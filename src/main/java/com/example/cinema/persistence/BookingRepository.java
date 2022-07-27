package com.example.cinema.persistence;

import com.example.cinema.model.entities.booking.Booking;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.session.MovieSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser(User user);

    List<Booking> findByMovieSession(MovieSession session);
}
