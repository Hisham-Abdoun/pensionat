package org.example.pensionat.config;

import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Room;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(RoomRepository roomRepository,
                               BookingRepository bookingRepository) {
        return args -> {
            if (roomRepository.count() > 0) {
                return;
            }

            Room rum101 = roomRepository.save(room(101, RoomType.SINGLE, 0, 650));
            Room rum102 = roomRepository.save(room(102, RoomType.SINGLE, 0, 650));
            Room rum201 = roomRepository.save(room(201, RoomType.DOUBLE, 0, 950));
            Room rum202 = roomRepository.save(room(202, RoomType.DOUBLE, 1, 1050));

            // OBS: customerId nedan är exempel-id:n.
            // De motsvarande kunderna måste finnas i kundtjänsten (port 8081)
            // för att bokningarna ska gå att skapa via API:et i verkligheten.
            bookingRepository.save(booking(1L, rum101,
                    LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 14), 1));
            bookingRepository.save(booking(2L, rum201,
                    LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), 2));
            bookingRepository.save(booking(3L, rum202,
                    LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 20), 3));
        };
    }

    private static Room room(int roomNumber, RoomType roomType,
                             int extraBeds, double pricePerNight) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setExtraBeds(extraBeds);
        room.setPricePerNight(pricePerNight);
        return room;
    }

    private static Booking booking(Long customerId, Room room,
                                   LocalDate startDate, LocalDate endDate,
                                   int numberOfGuests) {
        Booking booking = new Booking();
        booking.setCustomerId(customerId);
        booking.setRoom(room);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setNumberOfGuests(numberOfGuests);
        return booking;
    }
}
