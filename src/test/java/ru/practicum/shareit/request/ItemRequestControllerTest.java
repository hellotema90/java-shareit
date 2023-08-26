package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String userIdInHeader = "X-Sharer-User-Id";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("Description11")
            .created(LocalDateTime.now().plusHours(1))
            .items(null)
            .build();
    private final ItemRequestDto inItemRequestDto = ItemRequestDto.builder().id(2L).description("Description2")
            .build();

    @Test
    void createIsOk() throws Exception {
        when(itemRequestService.addItemRequest(any(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header(userIdInHeader, 1L)
                        .content(mapper.writeValueAsString(inItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.items").value(itemRequestDto.getItems()))
                .andExpect(jsonPath("$.created").value(itemRequestDto.getCreated().format(dateTimeFormatter)));
        verify(itemRequestService).addItemRequest(any(), anyLong());
    }

    @Test
    void getUserRequestsIsOk() throws Exception {
        when(itemRequestService.getUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .header(userIdInHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].items").value(itemRequestDto.getItems()));
    }

    @Test
    void getUserRequestsWithEmptyResponse() throws Exception {
        when(itemRequestService.getUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of());
        mvc.perform(get("/requests")
                        .header(userIdInHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    void getUserRequestsWithNotFoundUser() throws Exception {
        when(itemRequestService.getUserRequests(anyLong(), anyInt(), anyInt())).thenThrow(NotFoundException.class);
        mvc.perform(get("/requests")
                        .header(userIdInHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOtherUsersRequestsIsOk() throws Exception {
        when(itemRequestService.getOtherUserRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .header(userIdInHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$[0].items").value(itemRequestDto.getItems()));
    }

    @Test
    void getRequestByIdIsOk() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(userIdInHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.items").isEmpty());
        verify(itemRequestService).getItemRequestById(anyLong(), anyLong());
    }

    @Test
    void findByIdWithNotFound() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong())).thenThrow(NotFoundException.class);
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(userIdInHeader, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
