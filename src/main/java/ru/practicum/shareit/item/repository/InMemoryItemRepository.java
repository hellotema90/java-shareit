package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>();
    private final AtomicInteger generatorId = new AtomicInteger(0);

    private int countGeneratorId() {
        return generatorId.incrementAndGet();
    }

    @Override
    public Item addItem(Item item) {
        item.setId(countGeneratorId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItemById(int id) {
        return items.get(id);
    }

    @Override
    public void deleteItemById(int id) {
        items.remove(items.get(id));
    }

    @Override
    public List<Item> getAllItemsByUser(int userId) {
        return new ArrayList<>(items.values().stream()
                .filter(i -> (i.getOwner() != null && i.getOwner().getId() == userId))
                .collect(Collectors.toList()));
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getItemByText(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        item.getName().toLowerCase().contains(text)
                                || item.getDescription().toLowerCase().contains(text)
                )
                .collect(Collectors.toList());
    }
}
