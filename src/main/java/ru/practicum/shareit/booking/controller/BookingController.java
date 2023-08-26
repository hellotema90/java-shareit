package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
                                       @Valid @RequestBody InputBookingDto bookingDto) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public OutputBookingDto updateBooking(@PathVariable long bookingId, @RequestHeader(userIdInHeader) long userId,
                                          @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public OutputBookingDto getBookingDtoById(@PathVariable long bookingId,
                                              @RequestHeader(userIdInHeader) long userId) {
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    @GetMapping
    public List<OutputBookingDto> getBookingsOfBooker(@RequestParam(defaultValue = "ALL") String state,
                                                      @RequestHeader(userIdInHeader) Long bookerId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                      @RequestParam(defaultValue = "30") @Positive int size) {
        return bookingService.getBookingsOfBooker(state, bookerId, from, size);
    }

    @GetMapping("/owner")
    List<OutputBookingDto> getBookingsOfOwner(@RequestParam(defaultValue = "ALL") String state,
                                              @RequestHeader(userIdInHeader) Long ownerId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "20") @Positive int size) {
        return bookingService.getBookingsOfOwner(state, ownerId, from, size);
    }
}