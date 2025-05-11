package com.rentalmanagement.bookingservice.service;

import com.rentalmanagement.bookingservice.client.CustomerClient;
import com.rentalmanagement.bookingservice.client.RoomClient;
import com.rentalmanagement.bookingservice.exception.ResourceNotFoundException;
import com.rentalmanagement.bookingservice.model.Booking;
import com.rentalmanagement.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final CustomerClient customerClient;
    private final RoomClient roomClient;

    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        // Lấy thông tin chi tiết khách hàng và phòng cho mỗi booking
        bookings.forEach(this::enrichBookingWithDetails);
        return bookings;
    }

    public Booking getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng với ID: " + id));
        return enrichBookingWithDetails(booking);
    }

    public List<Booking> getBookingsByCustomerId(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        bookings.forEach(this::enrichBookingWithDetails);
        return bookings;
    }

    public List<Booking> searchBookings(Long customerId, Long bookingId) {
        List<Booking> bookings = bookingRepository.findByCustomerIdOrBookingId(customerId, bookingId);
        bookings.forEach(this::enrichBookingWithDetails);
        return bookings;
    }

    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        bookings.forEach(this::enrichBookingWithDetails);
        return bookings;
    }

    public Booking createBooking(Booking booking) {
        // Kiểm tra khách hàng
        CustomerClient.Customer customer = customerClient.getCustomerById(booking.getCustomerId()).getBody();
        if (customer == null) {
            throw new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + booking.getCustomerId());
        }

        // Kiểm tra phòng
        RoomClient.Room room = roomClient.getRoomById(booking.getRoomId()).getBody();
        if (room == null) {
            throw new ResourceNotFoundException("Không tìm thấy phòng với ID: " + booking.getRoomId());
        }

        // Cập nhật trạng thái phòng thành BOOKED
        roomClient.updateRoomStatus(booking.getRoomId(), "BOOKED");

        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);
        return enrichBookingWithDetails(savedBooking);
    }

    public Booking updateBooking(Long id, Booking booking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng với ID: " + id));

        // Nếu thay đổi phòng, cập nhật trạng thái phòng cũ và mới
        if (!existingBooking.getRoomId().equals(booking.getRoomId())) {
            // Cập nhật phòng cũ thành AVAILABLE
            roomClient.updateRoomStatus(existingBooking.getRoomId(), "AVAILABLE");

            // Cập nhật phòng mới thành BOOKED
            roomClient.updateRoomStatus(booking.getRoomId(), "BOOKED");
        }

        booking.setId(id);
        booking.setBookingDate(existingBooking.getBookingDate());
        booking.setCreatedAt(existingBooking.getCreatedAt());

        Booking updatedBooking = bookingRepository.save(booking);
        return enrichBookingWithDetails(updatedBooking);
    }

    public Booking updateBookingStatus(Long id, Booking.BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng với ID: " + id));

        // Nếu hủy đặt phòng, cập nhật trạng thái phòng thành AVAILABLE
        if (status == Booking.BookingStatus.CANCELLED) {
            roomClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");
        }

        booking.setStatus(status);
        Booking updatedBooking = bookingRepository.save(booking);

        return enrichBookingWithDetails(updatedBooking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng với ID: " + id));

        // Cập nhật trạng thái phòng thành AVAILABLE
        roomClient.updateRoomStatus(booking.getRoomId(), "AVAILABLE");

        bookingRepository.delete(booking);
    }

    // Helper method để lấy thông tin chi tiết khách hàng và phòng
    private Booking enrichBookingWithDetails(Booking booking) {
        try {
            // Lấy thông tin chi tiết khách hàng
            CustomerClient.Customer customer = customerClient.getCustomerById(booking.getCustomerId()).getBody();
            if (customer != null) {
                booking.setCustomerName(customer.getFullName());
            }

            // Lấy thông tin chi tiết phòng
            RoomClient.Room room = roomClient.getRoomById(booking.getRoomId()).getBody();
            if (room != null) {
                booking.setRoomNumber(room.getRoomNumber());
            }
        } catch (Exception e) {
            // Xử lý khi dịch vụ khác không khả dụng
        }
        return booking;
    }
}