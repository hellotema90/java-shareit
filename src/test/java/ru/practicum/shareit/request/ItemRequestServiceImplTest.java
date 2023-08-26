package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private Item item;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private ItemRequestDto inputItemRequestDto;

    @BeforeEach
    void beforeEach() {
        user = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        item = Item.builder().name("item1").description("item description1").available(true).owner(user).build();
        inputItemRequestDto = ItemRequestDto.builder().description("Input item Request description").build();
        itemRequest = ItemRequest.builder().id(1L).created(LocalDateTime.now())
                .description("Item Request description")
                .requestor(user).items(List.of(item)).build();
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        item.setRequest(itemRequest);
    }

    @Test
    void addWithOk() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        itemRequest.setDescription(inputItemRequestDto.getDescription());
        ItemRequestDto actual = itemRequestService.addItemRequest(inputItemRequestDto, user.getId());
        assertEquals(1L, actual.getId());
        assertEquals(inputItemRequestDto.getDescription(), actual.getDescription());
        assertNotNull(actual.getCreated());
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    void addWithNotFoundUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemRequestService.addItemRequest(inputItemRequestDto, user.getId()));
        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getUserRequestsWithOk() {
        when(userRepository.existsUserById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());
        List<ItemRequestDto> actual = itemRequestService.getUserRequests(user.getId(), 1, 1);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(itemRequest.getId(), actual.get(0).getId());
        assertEquals(itemRequest.getDescription(), actual.get(0).getDescription());
        assertEquals(List.of(), actual.get(0).getItems());
    }

    @Test
    void getOtherUserRequestsWithOk() {
        when(userRepository.existsUserById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdNot(anyLong(), any())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestIdIn(any())).thenReturn(List.of());
        List<ItemRequestDto> actual = itemRequestService.getOtherUserRequests(user.getId(), 0, 1);
        assertFalse(actual.isEmpty());
        assertEquals(1, actual.size());
        assertEquals(itemRequest.getId(), actual.get(0).getId());
        assertEquals(itemRequest.getDescription(), actual.get(0).getDescription());
        assertEquals(List.of(), actual.get(0).getItems());
    }

    @Test
    void findByIdWithOk() {
        when(userRepository.existsUserById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemRequestDto actual = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());
        assertEquals(1L, actual.getId());
        assertEquals(itemRequest.getDescription(), actual.getDescription());
        assertNotNull(actual.getCreated());
        assertEquals(List.of(), actual.getItems());
    }
}
