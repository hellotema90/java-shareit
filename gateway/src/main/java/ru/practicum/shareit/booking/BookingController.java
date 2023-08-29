package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPostRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;
    private static final String userIdInHeader = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(userIdInHeader) @Positive long userId,
                                                @RequestBody @Valid BookingPostRequestDto requestDto) {
        return bookingClient.createBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(userIdInHeader) @Positive long ownerId,
                                                 @PathVariable @Positive long bookingId,
                                                 @RequestParam(value = "approved", required = false) boolean approved) {
        return bookingClient.approveBooking(bookingId, ownerId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(userIdInHeader) @Positive long userId,
                                             @PathVariable @Positive long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfBooker(@RequestHeader(userIdInHeader) long bookerId,
                                                      @RequestParam(value = "state", defaultValue = "ALL")
                                                      String state,
                                                      @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero
                                                      int from,
                                                      @RequestParam(name = "size", defaultValue = "10") @Positive
                                                      int size) {
        return bookingClient.getBookingsOfBooker(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader(userIdInHeader) long ownerId,
                                                     @RequestParam(value = "state", defaultValue = "ALL")
                                                     String state,
                                                     @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                     int from,
                                                     @Positive @RequestParam(name = "size", defaultValue = "10")
                                                     int size) {
        return bookingClient.getBookingsOfOwner(ownerId, state, from, size);
    }
}