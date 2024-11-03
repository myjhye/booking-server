// 예약된 객실

package com.booking.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookedRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long bookingId;

    @Column(name = "check_in")
    private LocalDate checkInDate;

    @Column(name = "check_out")
    private LocalDate checkOutDate;

    @Column(name = "guest_fullName")
    private String guestFullName;

    @Column(name = "guest_email")
    private String guestEmail;

    @Column(name = "adults")
    private int NumOfAdults;

    @Column(name = "children")
    private int NumOfChildren;

    @Column(name = "total_guest")
    private int totalNumOfGuest;

    @Column(name = "confirmation_code")
    private String bookingConfirmationCode;

    // BookedRoom(예약된 객실)이 특정 Room(객실)과 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    // 예약이 어느 Room(객실)과 관련이 있는지 저장
    private Room room;

    /*
        @ManyToOne: 여러 예약이 같은 Room과 연결될 수 있음
        fetch = FetchType.LAZY: 필요할 때만 Room 정보를 불러와서 성능을 높임
        @JoinColumn(name = "room_id"): 예약이 어느 Room인지 연결
     */

    // 전체 게스트 수
    public void calculateTotalNumberOfGuest(){
        this.totalNumOfGuest = this.NumOfAdults + NumOfChildren;
    }

    // 성인 수 설정 시 전체 게스트 수 자동 업데이트
    public void setNumOfAdults(int numOfAdults) {
        NumOfAdults = numOfAdults;
        calculateTotalNumberOfGuest();
    }

    // 어린이 수 설정 시 전체 게스트 수 자동 업데이트
    public void setNumOfChildren(int numOfChildren) {
        NumOfChildren = numOfChildren;
        calculateTotalNumberOfGuest();
    }

    // 예약 확인 코드 설정
    public void setBookingConfirmationCode(String bookingConfirmationCode) {
        this.bookingConfirmationCode = bookingConfirmationCode;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}