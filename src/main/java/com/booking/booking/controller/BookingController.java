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


    // Room 엔티티/BookedRoom 엔티티 -> RoomResponse DTO/BookingResponse DTO 변환
    // 예약 정보 반환 메소드에 사용 (getAllBookings, getBookingByConfirmationCode)
    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();

        // Room 엔티티 -> RoomResponse DTO 변환 (3개 필드만 변환)
        RoomResponse room = new RoomResponse(theRoom.getId(),
                                            theRoom.getRoomType(),
                                            theRoom.getRoomPrice());

        // BookedRoom 엔티티 -> BookingResponse DTO 변환 (전체 필드 변환)
        return new BookingResponse(booking.getBookingId(),
                                    booking.getCheckInDate(),
                                    booking.getCheckOutDate(),
                                    booking.getGuestFullName(),
                                    booking.getGuestEmail(),
                                    booking.getNumOfAdults(),
                                    booking.getNumOfChildren(),
                                    booking.getTotalNumOfGuest(),
                                    booking.getBookingConfirmationCode(),
                                    room);

    }
}
