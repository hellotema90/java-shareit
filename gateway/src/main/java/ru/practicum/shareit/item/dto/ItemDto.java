package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Long id;
    @NotEmpty(message = "название не может быть пустым")
    private String name;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    @NotNull(message = "доступность не может быть пустой")
    private Boolean available;
    @JsonIgnore
    private Long owner;
    private Long requestId;
    private List<CommentDto> comments;
}