package com.booking.booking.service;

import com.booking.booking.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingRoomService {
    List<BookedRoom> getAllBookingsByRoomId(Long roomId);
}
