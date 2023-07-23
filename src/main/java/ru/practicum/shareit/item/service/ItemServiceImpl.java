package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemDto addItem(int ownerId, Item item) {
        if (!userRepository.userExists(ownerId)) {
            throw new NotFoundException(String.format("пользователя с таким id: %s нет", ownerId));
        }
        User owner = userRepository.getUserById(ownerId);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.addItem(item));
    }

    public ItemDto updateItem(int ownerId, int itemId, Map<String, String> updates) {
        Item item = itemRepository.getAllItemsByUser(ownerId).stream()
                .filter(i -> i.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Item id: %s owner id: %s не найдена.", itemId,
                        ownerId)));

        if (updates.containsKey("name")) {
            String value = updates.get("name");
            log.info("Change name item {} owner {}", itemId, ownerId);
            item.setName(value);
        }
        if (updates.containsKey("description")) {
            String value = updates.get("description");
            item.setDescription(value);
        }
        if (updates.containsKey("available")) {
            item.setAvailable(Boolean.valueOf(updates.get("available")));
        }
        return ItemMapper.toItemDto(item);
    }

    public ItemDto getItemById(int itemId) {
        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    public void deleteItemById(int itemId) {
        itemRepository.deleteItemById(itemId);

    }

    public List<ItemDto> getAllItems(int ownerId) {
        return ItemMapper.toItemDtoList(itemRepository.getAllItemsByUser(ownerId));
    }

    public List<ItemDto> getItemByText(String text) {//
        text = text.toLowerCase();
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text;
        return itemRepository.getAllItems()
                .stream()
                .filter(i -> (i.getDescription().toLowerCase().contains(searchText)
                        || i.getName().toLowerCase().contains(searchText))
                        && i.getAvailable())
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
