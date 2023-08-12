package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final String userIdInHeader = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(userIdInHeader) long ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(ownerId, ItemMapper.toItem(itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchUpdate(@RequestHeader(userIdInHeader) long ownerId, @PathVariable long itemId,
                               @RequestBody Map<String, String> updates) {
        return itemService.updateItem(ownerId, itemId, updates);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable long itemId, @RequestHeader(userIdInHeader) long userId) {
        return itemService.getItemDtoById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(userIdInHeader) long ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestHeader(userIdInHeader) long userId,
                                       @RequestParam(name = "text") String text) {
        return itemService.getItemByText(userId, text);
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
