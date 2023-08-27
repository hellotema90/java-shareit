package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private static final String userIdInHeader = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public OutputBookingDto addBooking(@RequestHeader(userIdInHeader) long userId,
                                       @RequestBody InputBookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto updateBooking(@RequestHeader(userIdInHeader) long userId,
                                          @PathVariable long bookingId,
                                          @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBookingDtoById(@PathVariable long bookingId,
                                              @RequestHeader(userIdInHeader) long userId) {
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> getBookingsOfBooker(@RequestParam(required = false) String state,
                                                      @RequestHeader(userIdInHeader) Long bookerId,
                                                      @RequestParam(required = false) int from,
                                                      @RequestParam(required = false) int size) {
        return bookingService.getBookingsOfBooker(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    public List<OutputBookingDto> getBookingsOfOwner(@RequestParam(required = false) String state,
                                                     @RequestHeader(userIdInHeader) Long ownerId,
                                                     @RequestParam(required = false) int from,
                                                     @RequestParam(required = false) int size) {
        return bookingService.getBookingsOfOwner(state, ownerId, from, size);
    }
}