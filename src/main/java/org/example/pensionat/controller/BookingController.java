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
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boolean created = bookingService.createBooking(bookingDto);
        if (created) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Bokning skapad!");
        } else {
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
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        boolean updated = bookingService.updateBooking(id, bookingDto);
        if (updated) {
            return ResponseEntity.ok("Bokning uppdaterad!");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Rummet är redan bokat!");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Avboka bokning")
    @ApiResponse(responseCode = "200", description = "Bokning avbokad")
    public ResponseEntity<String> deleteBooking(
            @Parameter(description = "Boknings-ID") @PathVariable Long id) {
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
        return ResponseEntity.ok(roomService.getAvailableRooms(startDate, endDate, numberOfGuests));
    }
}