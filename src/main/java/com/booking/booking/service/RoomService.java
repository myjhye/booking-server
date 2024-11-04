/*
    Room 엔티티에 비즈니스 로직 수행할 메소드 선언
 */

package com.booking.booking.service;

import com.booking.booking.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface RoomService {
    Room addNewRoom(MultipartFile photo,
                    String roomType,
                    BigDecimal roomPrice) throws SQLException, IOException;
}
