package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InputBookingDto {
    @NotNull
    private Long itemId;
    @NotNull
    @FutureOrPresent(message = "Начало не может быть в прошлом")
    private LocalDateTime start;
    @NotNull
    @Future(message = "Конец не может быть в прошлом")
    private LocalDateTime end;
}
