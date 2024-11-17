package com.booking.booking.service;

import com.booking.booking.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingRoomServiceImpl implements BookingRoomService {

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return List.of();
    }
}
