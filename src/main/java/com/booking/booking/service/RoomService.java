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

public interface RoomService {

    // 신규 객실 추가
    Room addNewRoom(MultipartFile photo,
                    String roomType,
                    BigDecimal roomPrice) throws SQLException, IOException;

    // 객실 유형 조회
    List<String> getAllRoomTypes();
}
