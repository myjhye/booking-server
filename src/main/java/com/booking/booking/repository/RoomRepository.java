/*
    SQL 작성 없이 Spring Data JPA가 제공하는 메소드로 데이터베이스 연동
    Room 엔티티에 CRUD 제공
 */
package com.booking.booking.repository;

import com.booking.booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // 객실 유형 조회
    @Query("SELECT DISTINCT r.roomType FROM Room r")
    List<String> findDistinctRoomTypes();

    // 예약 가능 객실 검색
    @Query(
            "SELECT r " +
            "FROM Room r " +
            "WHERE r.roomType LIKE %:roomType% " +
            "AND r.id NOT IN (" +
                "SELECT br.room.id " +
                "FROM BookedRoom br " +
                "WHERE ((br.checkInDate <= :checkOutDate) AND (br.checkOutDate >= :checkInDate))" +
            ")"
    )
    List<Room> findAvailableRoomsByDatesAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
