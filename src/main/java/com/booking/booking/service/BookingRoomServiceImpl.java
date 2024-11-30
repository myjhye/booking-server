package com.booking.booking.service;

import com.booking.booking.exception.InvalidBookingRequestException;
import com.booking.booking.model.BookedRoom;
import com.booking.booking.model.Room;
import com.booking.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingRoomServiceImpl implements BookingRoomService {

    // final: 한번 값이 주입된 후에는 값 변경 X
    // 객실 예약 서비스
    private final BookingRepository bookingRepository;
    // 객실 정보 서비스
    private final RoomService roomService;


    // 특정 객실의 모든 예약 내역 조회
    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }



    // 객실 예약 --> 요청된 객실의 예약 가능 여부 확인 / 가능하면 예약 정보를 저장 / 예약 확인 코드 반환
    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {

        // 체크인 날짜가 체크아웃 날짜보다 이후인지 검증
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("체크인 날짜는 체크아웃 날짜 이전이어야 합니다!");
        }

        // 예약하려는 객실 정보 조회
        Room room = roomService.getRoomById(roomId).get();
        // 해당 객실의 기존 예약 목록 조회
        List<BookedRoom> existingBookings = room.getBookings();
        // 요청된 날짜에 예약 가능한지 확인
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);

        if (roomIsAvailable) {
            // 예약 확인 랜덤 코드 생성
            String bookingCode = RandomStringUtils.randomNumeric(10);

            // 객실 -> 예약 연결 --> 이 예약(bookingRequest)은 이 객실(room)에 대한 거야
            bookingRequest.setRoom(room);
            // 예약 -> 객실 연결 --> 이 객실(room)에 새 예약(bookingRequest)이 들어왔어
            room.getBookings().add(bookingRequest);

            room.setBooked(true);
            bookingRequest.setBookingConfirmationCode(bookingCode);

            // 전체 투숙객 수 계산
            calculateTotalGuests(bookingRequest);

            // 새 예약 데이터베이스에 저장
            bookingRepository.save(bookingRequest);

            // 예약 확인 코드 반환
            return bookingCode;
        }

        throw new InvalidBookingRequestException("죄송합니다. 해당 객실은 선택한 날짜에 예약이 불가합니다.");
    }



    // 요청된 날짜에 객실 예약 가능한지 확인
    private boolean roomIsAvailable(BookedRoom bookingRequest,
                                    List<BookedRoom> existingBookings) {

        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        // 다음 경우들에 해당하면 예약 불가:
                        // 1. 동일한 체크인 날짜
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate()) ||

                        // 2. 기존 예약 기간 내에 체크인하려는 경우
                        (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate()) &&
                                bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate())) ||

                        // 3. 기존 예약 기간 내에 체크아웃하려는 경우
                        (bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate()) &&
                                bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())) ||

                        // 4. 기존 예약 기간을 완전히 포함하는 경우
                        (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) &&
                                bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                );

    }

    // 전체 투숙객 수 계산
    private void calculateTotalGuests(BookedRoom booking) {
        int totalGuests = booking.getNumOfAdults() + booking.getNumOfChildren();
        booking.setTotalNumOfGuest(totalGuests);
    }


    // 특정 객실 예약 취소
    @Override
    public void cancelBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // 시스템의 전체 예약 내역 조회
    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    // 예약 확인 코드로 특정 예약 정보 조회
    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode);
    }

}
