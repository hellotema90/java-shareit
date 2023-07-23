package ru.practicum.shareit.user.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private int id;
    @NotBlank(message = "имя не может быть пустым")
    private String name;
    @Email
    @NotBlank(message = "у пользователя должна быть почта")
    private String email;
}
