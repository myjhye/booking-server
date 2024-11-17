/*
    RoomService 인터페이스 구현하여 실제 비즈니스 로직 수행
 */

package com.booking.booking.service;

import com.booking.booking.exception.ResourceNotFoundException;
import com.booking.booking.model.Room;
import com.booking.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    // 신규 객실 추가
    // 객실 정보 설정하고, 사진 파일을 BLOB으로 변환해 데이터베이스에 저장
    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws SQLException, IOException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        if (!file.isEmpty()) {
            byte[] photoBytes = file.getBytes();
            Blob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);
        }

        return roomRepository.save(room);
    }

    // 객실 유형 조회
    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    // 전체 객실 조회
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    // 개별 객실 이미지 조회
    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);

        if (theRoom.isEmpty()) {
            throw new ResourceNotFoundException("요청한 객실이 없음");
        }

        Blob photoBlob = theRoom.get().getPhoto();
        if (photoBlob != null) {
            try {
                return photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("사진을 불러오는 중 오류 발생", e);
            }
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> theRoom = roomRepository.findById(roomId);
        if(theRoom.isPresent()) {
            roomRepository.deleteById(roomId);
        }
    }
}
