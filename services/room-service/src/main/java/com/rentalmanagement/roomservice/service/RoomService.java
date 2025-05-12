package com.rentalmanagement.roomservice.service;

import com.rentalmanagement.roomservice.exception.ResourceNotFoundException;
import com.rentalmanagement.roomservice.model.Room;
import com.rentalmanagement.roomservice.model.RoomType;
import com.rentalmanagement.roomservice.repository.RoomRepository;
import com.rentalmanagement.roomservice.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
    }

    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với số phòng: " + roomNumber));
    }

    public List<Room> getRoomsByStatus(Room.RoomStatus status) {
        return roomRepository.findByStatus(status);
    }

    public Room createRoom(Room room) {
        // Kiểm tra RoomType có tồn tại
        if (room.getRoomType() != null && room.getRoomType().getId() != null) {
            RoomType roomType = roomTypeRepository.findById(room.getRoomType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + room.getRoomType().getId()));
            room.setRoomType(roomType);
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room room) {
        // Kiểm tra xem phòng có tồn tại hay không
        roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));

        // Kiểm tra RoomType có tồn tại
        if (room.getRoomType() != null && room.getRoomType().getId() != null) {
            RoomType roomType = roomTypeRepository.findById(room.getRoomType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + room.getRoomType().getId()));
            room.setRoomType(roomType);
        }

        room.setId(id);
        return roomRepository.save(room);
    }

    public Room updateRoomStatus(Long id, Room.RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));

        room.setStatus(status);
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));

        roomRepository.delete(room);
    }
}