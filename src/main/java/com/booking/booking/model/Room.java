// 전체 객실

/*
    데이터베이스와 직접 연결됨, 'room' 테이블과 매핑
    객실 정보 저장
 */

package com.booking.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked = false;

    @Lob
    private Blob photo;

    // Room이 여러 개의 BookedRoom(예약된 객실)에 연결 -> 한 방에 예약 여러 개 받음
    @OneToMany(mappedBy="room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings;

    /*
        @OneToMany: Room이 여러 BookedRoom과 연결됨
        mappedBy="room": BookedRoom 클래스의 room 필드로 관계 설정
        fetch = FetchType.LAZY: 필요할 때만 BookedRoom 데이터를 불러와서 성능을 높임
        cascade = CascadeType.ALL: Room에 대한 변경 사항(CRUD)이 BookedRoom에도 모두 적용됨
     */

    public Room() {
        this.bookings = new ArrayList<>();
    }

    // 신규 예약 추가
    public void addBooking(BookedRoom booking){
        if (bookings == null) {
            bookings = new ArrayList<>();
        }
        // 신규 예약 추가
        bookings.add(booking);
        // 예약된 객실 설정
        booking.setRoom(this);
        // 예약 상태로 변경
        isBooked = true;
        // 예약 확인 코드 생성
        String bookingCode = RandomStringUtils.randomNumeric(10);
        // 예약된 객실에 예약 코드 설정
        booking.setBookingConfirmationCode(bookingCode);
    }


}
