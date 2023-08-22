package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final String userIdInHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(userIdInHeader) long ownerId, @Valid @RequestBody @NotNull ItemDto itemDto) {
        return itemService.addItem(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchUpdate(@RequestHeader(userIdInHeader) long ownerId, @PathVariable long itemId,
                               @RequestBody @NotNull Map<String, String> updates) {
        return itemService.updateItem(ownerId, itemId, updates);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(userIdInHeader) long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(userIdInHeader) long ownerId,
                                     @Valid @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                     @Valid @RequestParam(defaultValue = "10") @Positive int size) {
        return itemService.getAllItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestHeader(userIdInHeader) long userId,
                                       @RequestParam(name = "text") String text,
                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                       @RequestParam(defaultValue = "10") @Positive int size) {
        if ((text == null) || (text.isBlank())) {
            return List.of();
        }
        return itemService.getItemByText(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable long itemId, @RequestHeader(userIdInHeader) long ownerId) {
        itemService.deleteItemById(itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(userIdInHeader) long userId, @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
