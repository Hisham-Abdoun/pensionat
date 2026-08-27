package org.example.pensionat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Rooms", description = "API för rumshantering")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    @Operation(summary = "Hämta alla rum")
    @ApiResponse(responseCode = "200", description = "Lista med alla rum")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Hämta rum via ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rum hittat"),
            @ApiResponse(responseCode = "404", description = "Rum hittades inte")
    })
    public ResponseEntity<RoomDto> getRoomById(
            @Parameter(description = "Rum-ID") @PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PostMapping
    @Operation(summary = "Skapa nytt rum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rum skapat"),
            @ApiResponse(responseCode = "400", description = "Valideringsfel")
    })
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomDto roomDto,
                                        BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        roomService.saveRoom(roomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Rummet sparades!");
    }
}