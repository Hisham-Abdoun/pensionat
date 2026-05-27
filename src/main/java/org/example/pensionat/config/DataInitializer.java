package org.example.pensionat.config;

import org.example.pensionat.model.Booking;
import org.example.pensionat.model.Customer;
import org.example.pensionat.model.Room;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.CustomerRepository;
import org.example.pensionat.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(CustomerRepository customerRepository,
                               RoomRepository roomRepository,
                               BookingRepository bookingRepository) {
        return args -> {
            if (customerRepository.count() > 0) {
                return;
            }

            Customer anna = customerRepository.save(customer("Anna", "Andersson",
                    "anna.andersson@example.se", "070-111 22 33"));
            Customer erik = customerRepository.save(customer("Erik", "Eriksson",
                    "erik.eriksson@example.se", "070-444 55 66"));
            Customer maria = customerRepository.save(customer("Maria", "Lindqvist",
                    "maria.lindqvist@example.se", "070-777 88 99"));
            Customer martin = customerRepository.save(customer("Martin", "Danmo",
                    "martin.danmo@example.se", "070-777 00 00"));

            Room rum101 = roomRepository.save(room(101, RoomType.SINGLE, 0, 650));
            Room rum102 = roomRepository.save(room(102, RoomType.SINGLE, 0, 650));
            Room rum201 = roomRepository.save(room(201, RoomType.DOUBLE, 0, 950));
            Room rum202 = roomRepository.save(room(202, RoomType.DOUBLE, 1, 1050));

            bookingRepository.save(booking(anna, rum101,
                    LocalDate.of(2026, 6, 10), LocalDate.of(2026, 6, 14), 1));
            bookingRepository.save(booking(erik, rum201,
                    LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), 2));
            bookingRepository.save(booking(maria, rum202,
                    LocalDate.of(2026, 8, 15), LocalDate.of(2026, 8, 20), 3));
        };
    }

    private static Customer customer(String firstName, String lastName,
                                   String email, String phone) {
        Customer customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setEmail(email);
        customer.setPhoneNumber(phone);
        return customer;
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

    private static Booking booking(Customer customer, Room room,
                                 LocalDate startDate, LocalDate endDate,
                                 int numberOfGuests) {
        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setRoom(room);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setNumberOfGuests(numberOfGuests);
        return booking;
    }
}
