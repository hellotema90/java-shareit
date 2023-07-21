package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemDto addItem(int ownerId, Item item);
    ItemDto updateItem(int ownerId, int itemId, Map<String, String> updates);
    ItemDto getItemById(int itemId);
    void deleteItemById(int itemId);
    List<ItemDto> getAllItems(int ownerId);
    List<ItemDto> getItemByText(String text);
}
