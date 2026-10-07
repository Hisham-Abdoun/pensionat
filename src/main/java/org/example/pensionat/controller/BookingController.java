package org.example.pensionat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.pensionat.dto.BookingDto;
import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.service.BookingService;
import org.example.pensionat.service.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "API för bokningshantering")
public class BookingController {

    private static final Logger log = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService,
                             RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Hämta alla bokningar")
    @ApiResponse(responseCode = "200", description = "Lista med alla bokningar")
    public ResponseEntity<List<BookingDto>> getAllBookings() {
        log.info("GET /api/bookings - Hämtar alla bokningar");
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Hämta bokning via ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bokning hittad"),
            @ApiResponse(responseCode = "404", description = "Bokning hittades inte")
    })
    public ResponseEntity<BookingDto> getBookingById(
            @Parameter(description = "Boknings-ID") @PathVariable Long id) {
        log.info("GET /api/bookings/{} - Hämtar bokning", id);
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PostMapping
    @Operation(summary = "Skapa ny bokning")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bokning skapad"),
            @ApiResponse(responseCode = "400", description = "Valideringsfel eller rum redan bokat"),
            @ApiResponse(responseCode = "409", description = "Rummet är redan bokat")
    })
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                            BindingResult result) {
        log.info("POST /api/bookings - Skapar bokning för rum {} från {} till {}", 
                bookingDto.getRoomId(), bookingDto.getStartDate(), bookingDto.getEndDate());
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boolean created = bookingService.createBooking(bookingDto);
        if (created) {
            log.info("Bokning skapad framgångsrikt");
            return ResponseEntity.status(HttpStatus.CREATED).body("Bokning skapad!");
        } else {
            log.warn("Bokning misslyckades - rummet är redan bokat");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Rummet är redan bokat!");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Uppdatera bokning")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bokning uppdaterad"),
            @ApiResponse(responseCode = "400", description = "Valideringsfel"),
            @ApiResponse(responseCode = "404", description = "Bokning hittades inte"),
            @ApiResponse(responseCode = "409", description = "Rummet är redan bokat")
    })
    public ResponseEntity<?> updateBooking(
            @Parameter(description = "Boknings-ID") @PathVariable Long id,
            @Valid @RequestBody BookingDto bookingDto,
            BindingResult result) {
        log.info("PUT /api/bookings/{} - Uppdaterar bokning för rum {} från {} till {}", 
                id, bookingDto.getRoomId(), bookingDto.getStartDate(), bookingDto.getEndDate());
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boolean updated = bookingService.updateBooking(id, bookingDto);
        if (updated) {
            log.info("Bokning {} uppdaterad framgångsrikt", id);
            return ResponseEntity.ok("Bokning uppdaterad!");
        } else {
            log.warn("Uppdatering av bokning {} misslyckades - rummet är redan bokat", id);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Rummet är redan bokat!");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Avboka bokning")
    @ApiResponse(responseCode = "200", description = "Bokning avbokad")
    public ResponseEntity<String> deleteBooking(
            @Parameter(description = "Boknings-ID") @PathVariable Long id) {
        log.info("DELETE /api/bookings/{} - Avbokar bokning", id);
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Bokning avbokad!");
    }

    @GetMapping("/search")
    @Operation(summary = "Sök tillgängliga rum")
    @ApiResponse(responseCode = "200", description = "Lista med tillgängliga rum")
    public ResponseEntity<List<RoomDto>> searchAvailableRooms(
            @Parameter(description = "Startdatum") @RequestParam LocalDate startDate,
            @Parameter(description = "Slutdatum") @RequestParam LocalDate endDate,
            @Parameter(description = "Antal gäster") @RequestParam Integer numberOfGuests) {
        log.info("GET /api/bookings/search - Söker tillgängliga rum från {} till {} för {} gäster", 
                startDate, endDate, numberOfGuests);
        return ResponseEntity.ok(roomService.getAvailableRooms(startDate, endDate, numberOfGuests));
    }
}