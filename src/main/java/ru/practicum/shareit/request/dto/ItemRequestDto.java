package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private int id;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    private User requestor;
    @NotNull
    private LocalDate created;
}
