package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(long ownerId, ItemDto itemDto);

    ItemDto updateItem(long ownerId, long itemId, Map<String, String> updates);

    Item getItemById(long itemId);

    ItemDto getItemDtoById(long itemId, long userId);

    void deleteItemById(long ownerId, long itemId);

    List<ItemDto> getAllItems(long ownerId, int from, int size);

    List<ItemDto> getItemByText(String text, int from, int size);

    CommentDto addComment(long userId, long itemId, CommentDto commentDto);
}
