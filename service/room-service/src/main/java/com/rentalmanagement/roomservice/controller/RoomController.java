package com.rentalmanagement.roomservice.controller;

import com.rentalmanagement.roomservice.model.Room;
import com.rentalmanagement.roomservice.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<Room> getRoomByNumber(@PathVariable String roomNumber) {
        return ResponseEntity.ok(roomService.getRoomByNumber(roomNumber));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getRoomsByStatus(Room.RoomStatus.AVAILABLE));
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        return new ResponseEntity<>(roomService.createRoom(room), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        return ResponseEntity.ok(roomService.updateRoom(id, room));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Room> updateRoomStatus(@PathVariable Long id, @RequestBody String status) {
        // Chuyển đổi chuỗi thành enum
        Room.RoomStatus roomStatus = Room.RoomStatus.valueOf(status);
        return ResponseEntity.ok(roomService.updateRoomStatus(id, roomStatus));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}