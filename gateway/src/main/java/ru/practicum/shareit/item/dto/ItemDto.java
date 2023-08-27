package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(groups = Create.class, message = "название не может быть пустым")
    private String name;
    @NotBlank(groups = Create.class, message = "описание не может быть пустым")
    private String description;
    @NotNull(groups = Create.class, message = "доступность не может быть пустой")
    private Boolean available;
    @JsonIgnore
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments;
}