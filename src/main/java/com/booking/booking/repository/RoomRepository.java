/*
    SQL 작성 없이 Spring Data JPA가 제공하는 메소드로 데이터베이스 연동
    Room 엔티티에 CRUD 제공
 */
package com.booking.booking.repository;

import com.booking.booking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
