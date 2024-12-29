package com.booking.booking.service;

import com.booking.booking.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingRoomService {

    // 특정 객실의 모든 예약 내역 조회
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    // 예약 확인 코드로 특정 예약 정보 조회
    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    // 객실 예약 / 예약 확인 코드 반환
    String saveBooking(Long roomId, BookedRoom bookingRequest);

    // 특정 객실 예약 취소
    void cancelBooking(Long bookingId);

    // 시스템의 전체 예약 내역 조회
    List<BookedRoom> getAllBookings();

    // 개인 예약 조회
    List<BookedRoom> getBookingsByUserEmail(String email);
}
