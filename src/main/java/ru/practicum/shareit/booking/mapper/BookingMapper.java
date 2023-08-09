package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static OutputBookingDto toBookingDtoRequest(Booking booking) {
        return booking == null ? null : OutputBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static ShortBookingDto bookingDtoShort(Booking booking) {
        return booking == null ? null : ShortBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public static List<OutputBookingDto> toBookingDtoRequestsList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoRequest)
                .collect(Collectors.toList());
    }
}
