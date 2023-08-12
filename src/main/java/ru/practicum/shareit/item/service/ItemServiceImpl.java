package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.InternalServerError;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id %d не найден", userId)));
    }

    @Transactional
    public ItemDto addItem(long ownerId, Item item) {
        User owner = getUserById(ownerId);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(long ownerId, long itemId, Map<String, String> updates) {
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException(String.format("пользователь с id:%s не владелец вещи с id: %s", ownerId, itemId));
        }
        if (updates.containsKey("name")) {
            String value = updates.get("name");
            log.info("изменить название вещи {} владелец {}", itemId, ownerId);
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

    @Transactional(readOnly = true)
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id %d не найден", itemId)));
    }

    @Transactional(readOnly = true)
    public Long getOwnerId(long itemId) {
        Item item = getItemById(itemId);
        User owner = item.getOwner();
        if (owner == null) throw new InternalServerError("вещь с id = %d не имеет владельца");
        return owner.getId();
    }

    @Transactional(readOnly = true)
    public ItemDto getItemDtoById(long itemId, long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            setBookings(itemDto, bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED));
        }
        setComments(itemDto, comments);
        return itemDto;
    }

    @Transactional(readOnly = true)
    public List<ItemDto> getAllUserItems(long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(userId, BookingStatus.APPROVED);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), Sort.by(Sort.Direction.ASC, "created"));
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(items);
        itemDtos.forEach(i -> {
            setBookings(i, bookings);
            setComments(i, comments);
        });
        return itemDtos;
    }

    private void setBookings(ItemDto itemDto, List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        Long itemId = itemDto.getId();
        itemDto.setLastBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isBefore(now))
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .limit(1)
                .map(BookingMapper::bookingDtoShort)
                .findFirst().orElse(null));
        itemDto.setNextBooking(bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .filter(booking -> booking.getStart().isAfter(now))
                .sorted(Comparator.comparing(Booking::getStart))
                .limit(1)
                .map(BookingMapper::bookingDtoShort)
                .findFirst().orElse(null));
    }

    private void setComments(ItemDto itemDto, List<Comment> comments) {
        Long itemId = itemDto.getId();
        itemDto.setComments(comments.stream()
                .filter(comment -> comment.getItem().getId().equals(itemId))
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
    }

    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id = %d не найден", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("вещь с id = %d не найдена", itemId)));
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED,
                        LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(String.format("пользователь с id = %d не бронировал.", userId)));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public void deleteItemById(long ownerId, long itemId) {
        Item item = getItemById(itemId);
        if (!getOwnerId(itemId).equals(ownerId)) {
            throw new NotFoundException(String.format("пользователь с id:%d не владелец вещи с id:%d не найдена.",
                    ownerId, itemId));
        }
        itemRepository.delete(item);
    }

    public List<ItemDto> getAllItems(long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.APPROVED);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), Sort.by(Sort.Direction.ASC, "created"));
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(items);
        itemDtos.forEach(i -> {
            setBookings(i, bookings);
            setComments(i, comments);
        });
        return itemDtos;
    }

    public List<ItemDto> getItemByText(long userId, String text) {

        text = text.toLowerCase();
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDtoList(itemRepository.searchAvailableItems(text));
    }
}
