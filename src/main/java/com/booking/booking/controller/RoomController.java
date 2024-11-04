// 클라이언트의 HTTP 요청 처리

package com.booking.booking.controller;

import com.booking.booking.model.Room;
import com.booking.booking.response.RoomResponse;
import com.booking.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    // 신규 객실 추가
    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo,
                                                   @RequestParam("roomType") String roomType,
                                                   @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {

        // 클라이언트로부터 객실 정보 데이터(사진, 객실 타입, 가격)를 @RequestParam으로 받아서 roomService.addNewRoom 호출
        Room savedRoom = roomService.addNewRoom(photo, roomType, roomPrice);
        // 추가된 객실 정보를 RoomResponse 객체(DTO)로 변환해 클라이언트에 전달
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(), savedRoom.getRoomPrice());

        return ResponseEntity.ok(response);
    }
}
