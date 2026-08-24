package org.example.pensionat.service;

import org.example.pensionat.dto.RoomDto;
import org.example.pensionat.model.Room;
import org.example.pensionat.model.RoomType;
import org.example.pensionat.repository.BookingRepository;
import org.example.pensionat.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomService(RoomRepository roomRepository,
                       BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }


    private RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomType(room.getRoomType());
        dto.setExtraBeds(room.getExtraBeds());
        dto.setPricePerNight(room.getPricePerNight());
        return dto;
    }


    private Room toEntity(RoomDto dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomType(dto.getRoomType());
        room.setExtraBeds(dto.getExtraBeds());
        room.setPricePerNight(dto.getPricePerNight());
        return room;
    }


    public List<RoomDto> getAllRooms() {
        List<RoomDto> list = new ArrayList<>();

        for (Room r : roomRepository.findAll()) {
            list.add(toDto(r));
        }

        return list;
    }


    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return toDto(room);
    }


    public void saveRoom(RoomDto dto) {
        roomRepository.save(toEntity(dto));
    }


    public List<RoomDto> getAvailableRooms(LocalDate startDate,
                                           LocalDate endDate,
                                           int numberOfGuests) {

        List<RoomDto> available = new ArrayList<>();

        List<Room> rooms = roomRepository.findAll();

        for (Room room : rooms) {

            int maxGuests = getMaxGuests(room);

            if (maxGuests < numberOfGuests) {
                continue;
            }

            List conflicts = bookingRepository.findConflictingBookings(room, startDate, endDate);

            if (conflicts.isEmpty()) {
                available.add(toDto(room));
            }
        }

        return available;
    }


    private int getMaxGuests(Room room) {
        if (room.getRoomType() == RoomType.SINGLE) {
            return 1;
        } else {
            return 2 + room.getExtraBeds();
        }
    }
}