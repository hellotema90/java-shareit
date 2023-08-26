package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "название не может быть пустым")
    private String name;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    @JsonIgnore
    private User owner;
    private Long requestId;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentDto> comments;
}
