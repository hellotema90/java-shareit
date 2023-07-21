package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(Item item);
    Item updateItem(Item item);
    Item getItemById(int id);
    void deleteItemById(int id);
    List<Item> getAllItemsByUser(int userId);
    List<Item> getAllItems();
    List<Item> getItemByText(String text);
}
