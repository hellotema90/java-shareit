package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private long id;
    @NotBlank(message = "имя не может быть пустым")
    private String name;
    @Email
    @NotBlank(message = "почта не может быть пустой")
    private String email;
}
