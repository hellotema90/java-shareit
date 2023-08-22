package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private static final String userIdInHeader = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(userIdInHeader) Long userId,
                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(userIdInHeader) Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return itemRequestService.getUserRequests(userId, from, size);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(@RequestHeader(userIdInHeader) Long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        return itemRequestService.getOtherUserRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(userIdInHeader) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
