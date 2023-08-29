package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    OutputBookingDto addBooking(InputBookingDto bookingDto, Long userId);

    OutputBookingDto approveBooking(Long bookingId, Long userId, Boolean approve);

    Booking getBookingById(Long bookingId, Long userId);

    OutputBookingDto getBookingDtoById(Long bookingId, Long userId);

    List<OutputBookingDto> getBookingsOfBooker(String stateText, Long bookerId, int from, int size);

    List<OutputBookingDto> getBookingsOfOwner(String stateText, Long ownerId, int from, int size);
}
