package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Value
@AllArgsConstructor
public class CommentDto {
    private long id;
    @NotBlank(message = "Комментарий не может быть пустым")
    private String text;
    private String authorName;
    private String itemName;
    private LocalDateTime created;
}