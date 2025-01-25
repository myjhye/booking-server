// 객실 자체 정보
// 객실 번호, 가격, 객실 타입, 크기 등
// ex) 101호실


package com.booking.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Blob;
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

    // 한 객실의 예약 목록 -> 한 객실(Room)에 여러 예약 정보(BookedRoom)를 받는다 (1:N)
    @OneToMany(mappedBy="room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<BookedRoom> bookings;

    // 기본 생성자
    public Room() {

    }

}
