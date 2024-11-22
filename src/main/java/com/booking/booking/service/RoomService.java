/*
    Room 엔티티에 비즈니스 로직 수행할 메소드 선언
 */

package com.booking.booking.service;

import com.booking.booking.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    // 신규 객실 추가
    Room addNewRoom(MultipartFile photo,
                    String roomType,
                    BigDecimal roomPrice) throws SQLException, IOException;

    // 객실 유형 조회
    List<String> getAllRoomTypes();

    // 전체 객실 조회
    List<Room> getAllRooms();

    // 개별 객실 이미지 조회
    byte[] getRoomPhotoByRoomId(Long roomId);

    // 객실 개별 삭제
    void deleteRoom(Long roomId);

    // 객실 개별 수정
    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice);

    // 객실 개별 조회
    Optional<Room> getRoomById(Long roomId);
}
