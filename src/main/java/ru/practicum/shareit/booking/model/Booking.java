package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {
    private int id;
    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate end;
    private Item item;
    private User booker;
    private BookingStatus status;
}
