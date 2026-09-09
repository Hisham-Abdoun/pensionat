package org.example.pensionat.controller;

import org.example.pensionat.repository.BookingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Intern REST-API som andra tjänster (t.ex. kundtjänsten) anropar.
// Skiljer sig från BookingController som visar HTML-sidor.
@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    private final BookingRepository bookingRepository;

    public BookingApiController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Används av kundtjänsten för att kontrollera om en kund kan tas bort.
    // GET /api/bookings/customer/{customerId}/exists
    @GetMapping("/customer/{customerId}/exists")
    public ResponseEntity<Boolean> hasBookings(@PathVariable Long customerId) {
        boolean exists = bookingRepository.existsByCustomerId(customerId);
        return ResponseEntity.ok(exists);
    }
}