package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
