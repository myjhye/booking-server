package com.booking.booking.controller;

import com.booking.booking.exception.InvalidBookingRequestException;
import com.booking.booking.exception.ResourceNotFoundException;
import com.booking.booking.model.BookedRoom;
import com.booking.booking.model.Room;
import com.booking.booking.response.BookingResponse;
import com.booking.booking.response.RoomResponse;
import com.booking.booking.service.BookingRoomService;
import com.booking.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingRoomService bookingRoomService;
    private final RoomService roomService;

    // 전체 객실 예약 정보 조회
    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        // 1. 서비스에서 전체 예약 정보 조회
        List<BookedRoom> bookings = bookingRoomService.getAllBookings();
        // 2. 변환된 DTO를 담을 리스트 생성
        List<BookingResponse> bookingResponses = new ArrayList<>();

        // 3. 각 예약 정보(엔티티)를 DTO로 변환하고 리스트에 추가
        for (BookedRoom booking : bookings) {
            // 개별 예약 정보 변환 (방 정보 포함)
            BookingResponse bookingResponse = getBookingResponse(booking);
            // 변환된 정보를 리스트에 추가
            bookingResponses.add(bookingResponse);
        }

        // 4. 변환된 예약 목록을 HTTP 200(OK)와 함께 반환
        return ResponseEntity.ok(bookingResponses);
    }



    // 예약 코드를 사용해 특정 예약의 상세 정보 조회
    // ResponseEntity<?>: 성공 시: BookingResponse 타입 반환 / 실패 시: String 타입(에러 메세지) 반환
    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            // 1. 서비스로 예약 정보 조회
            BookedRoom booking = bookingRoomService.findByBookingConfirmationCode(confirmationCode);
            // 2. 엔티티를 DTO로 변환
            BookingResponse bookingResponse = getBookingResponse(booking);

            // 3. 성공 응답과 함께 예약 정보(bookingResponse) 반환
            return ResponseEntity.ok(bookingResponse);
        }
        catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    // 객실 예약 생성
    // /room/123/booking --> ID가 123인 객실에 대한 예약 생성
    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest) {

        try {
            // 객실 예약 정보 저장 후 예약 코드 반환
            String confirmationCode = bookingRoomService.saveBooking(roomId, bookingRequest);
            // 예약 성공 시 200 OK와 함께 예약 코드 포함한 메세지 반환
            return ResponseEntity.ok("생성된 예약 코드는 다음과 같습니다: " + confirmationCode);
        }
        catch (InvalidBookingRequestException e) {
            // 잘못된 예약 요청인 경우 400 Bad Request와 에러 메세지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 객실 예약 취소
    @DeleteMapping("/booking/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingRoomService.cancelBooking(bookingId);
    }


    // 개인 예약 조회
    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {

        // 1. 서비스 계층(bookingRoomService)을 통해 예약 엔티티(BookedRoom) 조회
        List<BookedRoom> bookings = bookingRoomService.getBookingsByUserEmail(email);

        // 2. 응답용 DTO 리스트 생성
        List<BookingResponse> bookingResponses = new ArrayList<>();

        // 3. 각 예약 엔티티(BookedRoom)를 DTO(BookingResponse)로 변환(getBookingResponse 사용)하여 리스트에 추가
        for (BookedRoom booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }

        // 4. 결과 반환
        return ResponseEntity.ok(bookingResponses);
    }


    // 엔티티 -> DTO 변환 (조회(GET)하는 메소드에서 재사용)
    private BookingResponse getBookingResponse(BookedRoom booking) {

        // 1. 예약된 객실 정보 조회 (Room 엔티티 가져오기)
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();

        // 2. Room 엔티티 -> RoomResponse DTO 변환 (3개 필드만 변환 -> 필요한 필드만 선택)
        RoomResponse room = new RoomResponse(
                                    theRoom.getId(),
                                    theRoom.getRoomType(),
                                    theRoom.getRoomPrice()
                                );

        // 3. BookedRoom 엔티티 -> BookingResponse DTO 변환 (전체 필드 변환)
        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfAdults(),
                booking.getNumOfChildren(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                room
        );

    }


}
