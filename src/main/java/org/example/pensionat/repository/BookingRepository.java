package org.example.pensionat.repository;

import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.room = :room " +
            "AND b.startDate < :endDate " +
            "AND b.endDate > :startDate")
    List<Booking> findConflictingBookings(
            @Param("room") Room room,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /** Samma som ovan men ignorerar en befintlig bokning (vid uppdatering). */
    @Query("SELECT b FROM Booking b WHERE b.room = :room " +
            "AND b.startDate < :endDate " +
            "AND b.endDate > :startDate " +
            "AND b.id <> :excludeBookingId")
    List<Booking> findConflictingBookingsExcluding(
            @Param("room") Room room,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeBookingId") Long excludeBookingId
    );
}