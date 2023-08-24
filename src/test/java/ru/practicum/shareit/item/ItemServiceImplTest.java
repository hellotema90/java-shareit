package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;
    private User user, user2;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private ItemRequest itemRequest;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
        item = Item.builder().id(1L).name("item1").description("itemDescription1").available(true)
                .owner(user).request(null).build();
        itemDto = ItemMapper.toItemDto(item);
        comment = Comment.builder().id(1L).text("textComment1").item(item).author(user)
                .created(LocalDateTime.now()).build();
        itemRequest = ItemRequest.builder().id(1L).created(LocalDateTime.now().plusMinutes(10))
                .description(item.getDescription()).requestor(user).items(List.of(item)).build();
        booking = Booking.builder().id(1L).item(item).booker(user).status(BookingStatus.APPROVED)
                .start(LocalDateTime.now()).end(LocalDateTime.now().plusHours(1)).build();
    }

    @Test
    void addItemIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto newItemDto = itemService.addItem(user.getId(), itemDto);
        assertNotNull(newItemDto);
        assertEquals(ItemDto.class, newItemDto.getClass());
        assertEquals(item.getId(), newItemDto.getId());
        assertEquals(item.getName(), newItemDto.getName());
        assertEquals(item.getDescription(), newItemDto.getDescription());
        assertEquals(item.getAvailable(), newItemDto.getAvailable());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void addItemWithBadUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addItem(23L, itemDto));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void addItemWithBadRequestId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        itemDto.setRequestId(0L);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addItem(user.getId(), itemDto));
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository).findById(anyLong());
    }

    @Test
    void updateIsOk() {
        Map<String, String> mapUpdate = Map.of(
                "name", "namUpdate",
                "description", "descriptionUpdate",
                "available", "false");
        item.setName(mapUpdate.get("name"));
        item.setDescription(mapUpdate.get("description"));
        item.setAvailable(Boolean.valueOf(mapUpdate.get("available")));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto newItem = itemService.updateItem(1L, 1L, mapUpdate);
        assertEquals(mapUpdate.get("name"), newItem.getName());
        assertEquals(mapUpdate.get("description"), newItem.getDescription());
        assertEquals(Boolean.valueOf(mapUpdate.get("available")), newItem.getAvailable());
        verify(itemRepository).save(any());
    }

    @Test
    void updateWithBadIdUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(0L, item.getId(), Map.of("a", "b")));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void updateWithNoOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(user2.getId(), item.getId(),
                Map.of("a", "b")));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void updateWithBadIdItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.updateItem(user.getId(), -1L,
                Map.of("a", "b")));
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItemByIdIsOk() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ItemDto findItem = ItemMapper.toItemDto(itemService.getItemById(item.getId()));
        assertEquals(item.getName(), findItem.getName());
        assertEquals(item.getDescription(), findItem.getDescription());
        assertNull(findItem.getLastBooking());
        assertNull(findItem.getNextBooking());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItemByIdWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getItemById(item.getId()));
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getAllIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        List<ItemDto> items = itemService.getAllItems(user.getId(), 0, 100);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(item.getId(), items.get(0).getId());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getAllWithEmptyCollection() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of());
        List<ItemDto> items = itemService.getAllItems(user.getId(), 0, 100);
        assertTrue(items.isEmpty());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findAllByOwnerId(anyLong(), any());
    }

    @Test
    void getAllWithEmptyUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getAllItems(anyLong(), 0, 100));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void searchAvailableItemsWithEmptyRequest() {
        List<ItemDto> items = itemService.getItemByText("", 0, 100);
        assertTrue(items.isEmpty());
    }

    @Test
    void searchAvailableItemsWithReturnCollection() {
        when(itemRepository.searchAvailableItems(anyString(), any(Pageable.class))).thenReturn(List.of(item));
        List<ItemDto> items = itemService.getItemByText("quEry", 0, 100);
        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
        assertEquals(items.get(0), ItemMapper.toItemDto(item));
        verify(itemRepository).searchAvailableItems(anyString(), any(Pageable.class));
    }

    @Test
    void addCommentIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(),
                any(), any(LocalDateTime.class))).thenReturn(Optional.of(booking));
        when(commentRepository.save(any())).thenReturn(comment);
        CommentDto actualComment = itemService.addComment(user.getId(), item.getId(),
                CommentMapper.toCommentDto(comment));
        assertNotNull(actualComment);
        assertEquals(CommentDto.class, actualComment.getClass());
        assertEquals(comment.getId(), actualComment.getId());
        assertEquals(comment.getText(), actualComment.getText());
        assertEquals(user.getName(), actualComment.getAuthorName());
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(),
                any(LocalDateTime.class));
    }

    @Test
    void addCommentWithoutBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(),
                any(), any(LocalDateTime.class))).thenReturn(Optional.empty());
        assertThrows(ValidationException.class,
                () -> itemService.addComment(user.getId(), item.getId(), CommentMapper.toCommentDto(comment)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findFirstByItemIdAndBookerIdAndStatusAndEndBefore(anyLong(), anyLong(), any(),
                any(LocalDateTime.class));
    }

    @Test
    void addCommentWithNotFoundItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(user.getId(), item.getId(), CommentMapper.toCommentDto(comment)));
        verify(userRepository).findById(anyLong());
        verify(itemRepository).findById(anyLong());
    }

    @Test
    void addCommentWithNotFoundUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> itemService.addComment(user.getId(), item.getId(), CommentMapper.toCommentDto(comment)));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void deleteIsOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        itemService.deleteItemById(user.getId(), item.getId());
        verify(itemRepository).delete(any());
    }

    @Test
    void deleteWithNotOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class,
                () -> itemService.deleteItemById(user2.getId(), item.getId()));
        verify(itemRepository, never()).delete(any());
    }

    @Test
    void getItemDtoByIdIsOk() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong(), any())).thenReturn(List.of(comment));
        ItemDto outItemDto = itemService.getItemDtoById(item.getId(), user.getId());
        assertEquals(outItemDto.getName(), item.getName());
        assertEquals(1, outItemDto.getComments().size());
        verify(bookingRepository).findAllByItemIdAndStatus(anyLong(), any(), any());
    }

    @Test
    void getItemDtoByIdIsWithNotOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(anyLong(), any())).thenReturn(List.of(comment));
        item.setOwner(user);
        ItemDto outItemDto = itemService.getItemDtoById(item.getId(), user2.getId());
        assertEquals(outItemDto.getName(), item.getName());
        verify(bookingRepository, never()).findAllByItemIdAndStatus(anyLong(), any(), any());
    }

}
