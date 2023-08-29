package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "имя не может быть пустым")
    private String name;
    @Email(message = "почта должна быть написана в формате: test@email.com!")
    @NotBlank(message = "почта не может быть пустой")
    private String email;
}