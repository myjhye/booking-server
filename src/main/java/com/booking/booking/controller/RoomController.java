// 클라이언트의 HTTP 요청 처리

package com.booking.booking.controller;

import com.booking.booking.exception.PhotoRetrievalException;
import com.booking.booking.exception.ResourceNotFoundException;
import com.booking.booking.model.BookedRoom;
import com.booking.booking.model.Room;
import com.booking.booking.response.BookingResponse;
import com.booking.booking.response.RoomResponse;
import com.booking.booking.service.BookingRoomService;
import com.booking.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;
    private final BookingRoomService bookingRoomService;


    // 객실 유형 조회
    @GetMapping("/room-types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }


    // 전체 객실 조회
    // ResponseEntity: 응답 성공 여부(ResponseEntity.ok)
    // <List<RoomResponse>>: 클라이언트에게 응답하는 형태
    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() {

        // roomService 호출하여 전체 객실 목록 가져오기
        List<Room> rooms = roomService.getAllRooms();

        // 클라이언트에 전달할 RoomResponse 객체들을 담을 리스트 생성
        List<RoomResponse> roomResponses = new ArrayList<>();

        // 각 객실의 이미지를 포함하여 RoomResponse로 변환
        for(Room room : rooms) {
            // room id로 관련 개별 객실 이미지 가져오기
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            // 객실 이미지가 있으면
            if (photoBytes != null && photoBytes.length > 0) {
                // 객실 이미지를 byte[] -> Base64 형식으로 인코딩 (BLOB 데이터 -> byte[] -> Base64)
                String base64Photo = Base64.getEncoder().encodeToString(photoBytes);

                // Room 객체 정보를 기반한 roomResponse 객체 생성
                RoomResponse roomResponse = getRoomResponse(room);
                // 인코딩된 객실 이미지 데이터를 roomResponse 객체의 photo 필드에 설정
                roomResponse.setPhoto(base64Photo);

                // RoomResponse 객체를 리스트에 추가
                roomResponses.add(roomResponse);
            }
        }

        // 모든 roomResponse 객체들이 담긴 리스트를 HTTP 응답으로 클라이언트에 반환
        return ResponseEntity.ok(roomResponses);
    }


    // 객실 개별 조회
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) {

        // roomService를 통해 roomId에 해당하는 객실 정보를 Optional<Room>으로 가져온다
        // Optional<Room>: 해당 ID의 객실이 있을 수도 있고 없을 수도 있다
        Optional<Room> theRoom = roomService.getRoomById(roomId);

        // 데이터가 없으면 예외 발생
        return theRoom.map((room) -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("해당 객실이 없습니다"));
    }


    // 예약 가능 객실 검색
    // @RequestParam: url에 쿼리 파라미터로 전달하는 값 --> /available-rooms?checkInDate=2024-12-04&checkOutDate=2024-12-05&roomType=deluxe
    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(@RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                                @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                                                @RequestParam("roomType") String roomType) {

        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();

        for (Room room : availableRooms) {
            // 현재 객실의 사진 데이터를 바이트 배열로 가져오기
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if (photoBytes != null && photoBytes.length > 0) {
                // 바이트 배열 --> Base64 문자열로 인코딩
                String photoBase64 = Base64.getEncoder().encodeToString(photoBytes);
                // Room 엔티티를 RoomResponse DTO로 변환
                RoomResponse roomResponse = getRoomResponse(room);
                // 변환된 DTO에 Base64로 인코딩된 사진 데이터 설정
                roomResponse.setPhoto(photoBase64);
                // 응답 리스트에 추가
                roomResponses.add(roomResponse);
            }
        }

        if (roomResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        else {
            // HTTP 200 OK 상태코드와 함께 데이터 반환
            return ResponseEntity.ok(roomResponses);
        }

    }




    // Room 엔티티 -> Room DTO 변환
    private RoomResponse getRoomResponse(Room room) {

        // 1. Room ID로 해당 객실의 전체 예약 내역을 조회
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());

        // 2. 예약 엔티티(BookedRoom) 목록을 DTO(BookingResponse) 목록으로 변환
        // stream()으로 순회하면서 각 BookedRoom을 BookingResponse로 변환(4개 필드만 사용)
        List<BookingResponse> bookingInfo = bookings
                                                .stream()
                                                .map(booking -> new BookingResponse(
                                                        booking.getBookingId(),
                                                        booking.getCheckInDate(),
                                                        booking.getCheckOutDate(),
                                                        booking.getBookingConfirmationCode())
                                                )
                                                .toList(); // 변환된 DTO 목록을 List로 수집

        // 3. Room 엔티티의 이미지(BLOB) 처리
        byte[] photoBytes = null; // 이미지 데이터를 저장할 바이트 배열
        Blob photoBlob = room.getPhoto(); // Room 엔티티에서 BLOB 형태의 이미지 데이터 가져오기
        if (photoBlob != null) {
            try {
                // BLOB 데이터를 byte[] 형태로 변환
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                e.printStackTrace();
                // 이미지 처리 중 오류 발생 시 커스텀 예외 발생
                throw new PhotoRetrievalException("객실 이미지 불러오는 중 오류 발생");
            }
        }

        // 4. Room 엔티티 정보를 기반으로 RoomResponse DTO 생성 후 반환
        return new RoomResponse(room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(),
                photoBytes,
                bookingInfo);
    }

    // 특정 객실의 예약 정보 조회
    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRoomService.getAllBookingsByRoomId(roomId);
    }



    // 신규 객실 추가
    @PostMapping("/add/new-room")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("roomType") String roomType,
                                                   @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {

        // 클라이언트로부터 객실 정보 데이터(사진, 객실 타입, 가격)를 @RequestParam으로 받아서 roomService.addNewRoom 호출
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        // 추가된 객실 정보를 RoomResponse 객체(DTO)로 변환해 클라이언트에 전달
        RoomResponse response = new RoomResponse(savedRoom.getId(),
                savedRoom.getRoomType(),
                savedRoom.getRoomPrice());

        return ResponseEntity.ok(response);
    }


    // 객실 개별 수정
    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice) throws SQLException, IOException {

        // roomService를 통해 객실 정보 수정
        Room theRoom = roomService.updateRoom(roomId, roomType, roomPrice);
        // 수정된 Room 객체를 roomResponse(DTO)로 변환 --> 클라이언트에 전달
        RoomResponse roomResponse = getRoomResponse(theRoom);

        return ResponseEntity.ok(roomResponse);

    }



    // 객실 개별 삭제
    // @PathVariable: 경로에 포함된 {roomId} 값을 추출해 deleteRoom 메소드의 매개변수인 roomId에 전달
    @DeleteMapping("/delete/room/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
