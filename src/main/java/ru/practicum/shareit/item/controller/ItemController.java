package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
    public ItemDto create(@RequestHeader(userIdInHeader) int ownerId, @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(ownerId, ItemMapper.toItem(itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto patchUpdate(@RequestHeader(userIdInHeader) int ownerId, @PathVariable int itemId,
                               @RequestBody Map<String, String> updates) {
        return itemService.updateItem(ownerId, itemId, updates);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader(userIdInHeader) int ownerId) {
        return itemService.getAllItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam(name = "text") String text) {
        return itemService.getItemByText(text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable int itemId) {
        itemService.deleteItemById(itemId);
    }
}
