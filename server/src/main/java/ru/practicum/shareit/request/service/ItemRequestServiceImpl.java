package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto addItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id %d не найден", userId)));
        ItemRequest itemRequest = itemRequestRepository.save(ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requestor(user)
                .build());
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    public List<ItemRequestDto> getUserRequests(Long userId, int from, int size) {
        existsUserById(userId);
        return itemRequestsToDto(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId,
                getPageRequest(from, size)));
    }


    public List<ItemRequestDto> getOtherUserRequests(Long userId, int from, int size) {
        existsUserById(userId);
        return itemRequestsToDto(itemRequestRepository.findAllByRequestorIdNot(userId, getPageRequest(from, size)));
    }


    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        existsUserById(userId);
        ItemRequest itemRequest = getItemRequestById(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, itemRepository.findAllByRequestId(requestId));
    }

    private List<ItemRequestDto> itemRequestsToDto(List<ItemRequest> itemRequests) {
        List<Long> itemRequestsIds = itemRequests
                .stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());
        Map<Long, List<Item>> itemsByRequest = itemRepository.findAllByRequestIdIn(itemRequestsIds)
                .stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return itemRequests
                .stream()
                .map(r -> ItemRequestMapper.toItemRequestDto(r, itemsByRequest.getOrDefault(r.getId(), List.of())))
                .collect(Collectors.toList());
    }

    private void existsUserById(Long userId) {
        if (!(userRepository.existsUserById(userId))) {
            throw new NotFoundException(String.format("пользователь с id %d не найден", userId));
        }
    }

    private PageRequest getPageRequest(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by(Sort.Direction.DESC, "created"));
    }

    private ItemRequest getItemRequestById(Long id) {
        return itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("запрос вещи с id: %d не найден", id)));
    }
}
