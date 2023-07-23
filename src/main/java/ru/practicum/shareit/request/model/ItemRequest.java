package ru.practicum.shareit.request.model;

import lombok.*;
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
@Data
public class ItemRequest {
    private  int id;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    private User requestor;
    @NotNull
    private LocalDate created;
}
