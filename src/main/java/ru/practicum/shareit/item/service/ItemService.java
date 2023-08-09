package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(long ownerId, Item item);

    ItemDto updateItem(long ownerId, long itemId, Map<String, String> updates);

    Item getItemById(long itemId);

    ItemDto getItemDtoById(long itemId, long userId);

    Long getOwnerId(long itemId);

    void deleteItemById(long ownerId, long itemId);

    List<ItemDto> getAllItems(long ownerId);

    List<ItemDto> getItemByText(long userId, String text);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
