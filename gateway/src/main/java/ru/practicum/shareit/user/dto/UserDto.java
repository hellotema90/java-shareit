package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.Create;
import ru.practicum.shareit.validation.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotBlank(groups = {Create.class}, message = "имя не может быть пустым")
    private String name;
    @Email(groups = {Create.class, Update.class}, message = "почта должна быть написана в формате: test@email.com!")
    @NotBlank(groups = {Create.class}, message = "почта не может быть пустой")
    private String email;
}