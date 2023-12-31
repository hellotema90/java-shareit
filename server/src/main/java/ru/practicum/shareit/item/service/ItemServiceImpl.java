package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final Sort sort = Sort.by(Sort.Direction.ASC, "created");

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id %d не найден", userId)));
    }

    @Transactional
    public ItemDto addItem(long ownerId, ItemDto itemDto) {
        User owner = getUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            Long requestId = itemDto.getRequestId();
            item.setRequest(itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException(
                            String.format("запрос с id:%s не найден ", requestId))));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        existsUserWithId(ownerId);
        Item item = getItemById(itemId);
        checkOwnerOfItem(ownerId, item);
        int i = 0;
        if ((itemDto.getName() != null) && (!itemDto.getName().isBlank())) {
            i++;
            item.setName(itemDto.getName());
        }
        if ((itemDto.getDescription() != null) && (!itemDto.getDescription().isBlank())) {
            i++;
            item.setDescription(itemDto.getDescription());
        }
        if ((itemDto.getAvailable() != null)) {
            i++;
            item.setAvailable(itemDto.getAvailable());
        }
        if (i > 0) {
            itemRepository.save(item);
        }
        return ItemMapper.toItemDto(item);
    }

    private void checkOwnerOfItem(Long ownerId, Item item) {
        User owner = item.getOwner();
        if ((owner == null) || (!owner.getId().equals(ownerId))) {
            throw new NotFoundException(String.format("пользователь с id:%s не владелец вещи с id: %s", ownerId,
                    item.getId()));
        }
    }

    private void existsUserWithId(Long userId) {
        if (!userRepository.existsUserById(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
    }

    @Transactional(readOnly = true)
    public Item getItemById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id %d не найден", itemId)));
    }

    @Transactional(readOnly = true)
    public ItemDto getItemDtoById(long itemId, long userId) {
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId(),
                Sort.by(Sort.Direction.ASC, "created"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            setBookings(itemDto, bookingRepository.findAllByItemIdAndStatus(itemId, BookingStatus.APPROVED,
                    PageRequest.of(0, 10000, BookingRepository.SORT_BY_START_DESC)));
        }
        setComments(itemDto, comments);
        return itemDto;
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
        getUserById(ownerId);
        Item item = getItemById(itemId);
        checkOwnerOfItem(ownerId, item);
        itemRepository.delete(item);
    }

    public List<ItemDto> getAllItems(long ownerId, int from, int size) {
        existsUserWithId(ownerId);
        List<Item> items = itemRepository.findAllByOwnerId(ownerId, PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "name")));
        Pageable pageable = PageRequest.of(from / size, size, BookingRepository.SORT_BY_START_DESC);
        List<Booking> bookings = bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.APPROVED, pageable);
        List<Comment> comments = commentRepository.findAllByItemIdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()), sort);
        List<ItemDto> itemsDto = ItemMapper.toItemDtoList(items);
        itemsDto.forEach(i -> {
            setBookings(i, bookings);
            setComments(i, comments);
        });
        return itemsDto;
    }

    public List<ItemDto> getItemByText(String text, int from, int size) {
        if ((text == null) || (text.isBlank())) {
            return List.of();
        }
        Pageable pageable = PageRequest.of(from / size, size);
        return ItemMapper.toItemDtoList(itemRepository.searchAvailableItems(text, pageable));
    }
}
