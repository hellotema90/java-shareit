package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "описание не может быть пустым")
    private String description;
    private List<ItemDto> items;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
}
