package com.rentalmanagement.bookingservice.service;

import com.rentalmanagement.bookingservice.client.CustomerClient;
import com.rentalmanagement.bookingservice.client.RoomClient;
import com.rentalmanagement.bookingservice.exception.ResourceNotFoundException;
import com.rentalmanagement.bookingservice.model.Booking;
import com.rentalmanagement.bookingservice.model.CheckIn;
import com.rentalmanagement.bookingservice.repository.BookingRepository;
import com.rentalmanagement.bookingservice.repository.CheckInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final CheckInRepository checkInRepository;
    private final BookingRepository bookingRepository;
    private final CustomerClient customerClient;
    private final RoomClient roomClient;

    public List<CheckIn> getAllCheckIns() {
        List<CheckIn> checkIns = checkInRepository.findAll();
        checkIns.forEach(this::enrichCheckInWithDetails);
        return checkIns;
    }

    public CheckIn getCheckInById(Long id) {
        CheckIn checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhận phòng với ID: " + id));
        return enrichCheckInWithDetails(checkIn);
    }

    public List<CheckIn> getCheckInsByCustomerId(Long customerId) {
        List<CheckIn> checkIns = checkInRepository.findByCustomerId(customerId);
        checkIns.forEach(this::enrichCheckInWithDetails);
        return checkIns;
    }

    public List<CheckIn> getCheckInsByRoomId(Long roomId) {
        List<CheckIn> checkIns = checkInRepository.findByRoomId(roomId);
        checkIns.forEach(this::enrichCheckInWithDetails);
        return checkIns;
    }

    public CheckIn getCheckInByBookingId(Long bookingId) {
        CheckIn checkIn = checkInRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhận phòng với ID đặt phòng: " + bookingId));
        return enrichCheckInWithDetails(checkIn);
    }

    public List<CheckIn> getCheckInsByStatus(CheckIn.CheckInStatus status) {
        List<CheckIn> checkIns = checkInRepository.findByStatus(status);
        checkIns.forEach(this::enrichCheckInWithDetails);
        return checkIns;
    }

    public CheckIn createCheckIn(CheckIn checkIn) {
        // Kiểm tra khách hàng và phòng
        CustomerClient.Customer customer = customerClient.getCustomerById(checkIn.getCustomerId()).getBody();
        RoomClient.Room room = roomClient.getRoomById(checkIn.getRoomId()).getBody();

        if (customer == null || room == null) {
            throw new ResourceNotFoundException("Không tìm thấy khách hàng hoặc phòng");
        }

        // Tạo booking mới
        Booking booking = new Booking();
        booking.setCustomerId(checkIn.getCustomerId());
        booking.setRoomId(checkIn.getRoomId());
        booking.setCheckInDate(checkIn.getCheckInDate() != null
                ? checkIn.getCheckInDate()
                : LocalDate.now()); // Ngày nhận phòng
        booking.setStatus(Booking.BookingStatus.CHECKED_IN); // Trạng thái là đã nhận phòng
        booking.setNotes("Nhận phòng trực tiếp"); // Ghi chú

        // Lưu booking
        Booking savedBooking = bookingRepository.save(booking);

        // Liên kết với booking vừa tạo
        checkIn.setBookingId(savedBooking.getId());

        // Đảm bảo ngày nhận phòng được thiết lập
        if (checkIn.getCheckInDate() == null) {
            checkIn.setCheckInDate(LocalDate.now());
        }

        // Đảm bảo trạng thái là ACTIVE
        checkIn.setStatus(CheckIn.CheckInStatus.ACTIVE);

        // Tính toán ngày dự kiến trả phòng (ví dụ: 1 tháng sau)
        checkIn.setExpectedCheckOutDate(checkIn.getCheckInDate().plusMonths(1));

        // Lưu check-in
        CheckIn savedCheckIn = checkInRepository.save(checkIn);

        // Cập nhật trạng thái phòng
        roomClient.updateRoomStatus(savedBooking.getRoomId(), "OCCUPIED");

        return enrichCheckInWithDetails(savedCheckIn);
    }

    public CheckIn createCheckInFromBooking(Long bookingId) {
        // 1. Lấy thông tin đặt phòng
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt phòng"));

        // 2. Kiểm tra trạng thái đặt phòng
        if (booking.getStatus() == Booking.BookingStatus.CHECKED_IN) {
            throw new IllegalStateException("Đặt phòng này đã được nhận phòng");
        }

        // 3. Cập nhật trạng thái đặt phòng
        booking.setStatus(Booking.BookingStatus.CHECKED_IN);
        bookingRepository.save(booking);

        // 4. Cập nhật trạng thái phòng
        roomClient.updateRoomStatus(booking.getRoomId(), "OCCUPIED");

        // 5. Tạo check-in mới
        CheckIn checkIn = new CheckIn();
        checkIn.setBookingId(bookingId);
        checkIn.setCustomerId(booking.getCustomerId());
        checkIn.setRoomId(booking.getRoomId());
        checkIn.setCheckInDate(LocalDate.now());
        checkIn.setStatus(CheckIn.CheckInStatus.ACTIVE);
        checkIn.setExpectedCheckOutDate(LocalDate.now().plusMonths(1));

        return enrichCheckInWithDetails(checkInRepository.save(checkIn));
    }

    public CheckIn updateCheckIn(Long id, CheckIn checkIn) {
        CheckIn existingCheckIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhận phòng với ID: " + id));

        // Nếu thay đổi phòng, cập nhật trạng thái phòng cũ và mới
        if (!existingCheckIn.getRoomId().equals(checkIn.getRoomId())) {
            // Cập nhật phòng cũ thành AVAILABLE
            roomClient.updateRoomStatus(existingCheckIn.getRoomId(), "AVAILABLE");

            // Cập nhật phòng mới thành OCCUPIED
            roomClient.updateRoomStatus(checkIn.getRoomId(), "OCCUPIED");
        }

        checkIn.setId(id);
        checkIn.setCreatedAt(existingCheckIn.getCreatedAt());

        CheckIn updatedCheckIn = checkInRepository.save(checkIn);
        return enrichCheckInWithDetails(updatedCheckIn);
    }

    public CheckIn updateCheckInStatus(Long id, CheckIn.CheckInStatus status) {
        CheckIn checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhận phòng với ID: " + id));

        // Nếu trả phòng, cập nhật trạng thái phòng thành AVAILABLE
        if (status == CheckIn.CheckInStatus.CHECKED_OUT) {
            roomClient.updateRoomStatus(checkIn.getRoomId(), "AVAILABLE");
        }

        checkIn.setStatus(status);
        CheckIn updatedCheckIn = checkInRepository.save(checkIn);

        return enrichCheckInWithDetails(updatedCheckIn);
    }

    public void deleteCheckIn(Long id) {
        CheckIn checkIn = checkInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhận phòng với ID: " + id));

        // Cập nhật trạng thái phòng thành AVAILABLE
        roomClient.updateRoomStatus(checkIn.getRoomId(), "AVAILABLE");

        checkInRepository.delete(checkIn);
    }

    // Helper method để lấy thông tin chi tiết khách hàng và phòng
    private CheckIn enrichCheckInWithDetails(CheckIn checkIn) {
        try {
            // Lấy thông tin chi tiết khách hàng
            CustomerClient.Customer customer = customerClient.getCustomerById(checkIn.getCustomerId()).getBody();
            if (customer != null) {
                checkIn.setCustomerName(customer.getFullName());
            }

            // Lấy thông tin chi tiết phòng
            RoomClient.Room room = roomClient.getRoomById(checkIn.getRoomId()).getBody();
            if (room != null) {
                checkIn.setRoomNumber(room.getRoomNumber());
            }
        } catch (Exception e) {
            // Xử lý khi dịch vụ khác không khả dụng
        }
        return checkIn;
    }
}