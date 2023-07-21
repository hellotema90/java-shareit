package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Item {
    private int id;
    @NotBlank(message = "название не может быть пустым")
    private String name;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    @NotNull
    private Boolean available;
    @NotNull
    @NotBlank(message = "вещь не может быть без хозяина")
    private User owner;
    private ItemRequest request;

}
