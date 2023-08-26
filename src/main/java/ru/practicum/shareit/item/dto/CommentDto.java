package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentDto {
    private long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
    private String authorName;
    private String itemName;
    private LocalDateTime created;
}
