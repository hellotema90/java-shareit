package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    User user1, user2, user3;
    Item item1, item2, item3;
    Booking booking1, booking2, booking3, booking4;
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end = LocalDateTime.now().plusHours(1);
    Pageable pageable = PageRequest.of(0, 10, BookingRepository.SORT_BY_START_BY_DESC);

    @BeforeEach
    void beforeEach() {
        user1 = User.builder().id(1L).name("user1").email("user1@mail.ru").build();
        user2 = User.builder().id(2L).name("user2").email("user2@mail.ru").build();
        user3 = User.builder().id(3L).name("user3").email("user3@mail.ru").build();
        item1 = Item.builder().id(1L).name("item1").description("iDescr1").available(true)
                .owner(user1).request(null).build();
        item2 = Item.builder().id(2L).name("item2").description("iDescr2").available(true)
                .owner(user1).request(null).build();
        item3 = Item.builder().id(3L).name("item3").description("iDescr3").available(true)
                .owner(user2).request(null).build();
        booking1 = Booking.builder().id(1L).item(item1).booker(user2).status(BookingStatus.APPROVED)
                .start(start).end(end).build();
        booking2 = Booking.builder().id(2L).item(item2).booker(user2).status(BookingStatus.APPROVED)
                .start(start.plusMinutes(10)).end(end.plusMinutes(10)).build();
        booking3 = Booking.builder().id(3L).item(item2).booker(user3).status(BookingStatus.APPROVED)
                .start(start.plusMinutes(20)).end(end.plusMinutes(20)).build();
        booking4 = Booking.builder().id(4L).item(item3).booker(user3).status(BookingStatus.APPROVED)
                .start(start.plusMinutes(30)).end(end.plusMinutes(30)).build();
        userRepository.saveAll(List.of(user1, user2, user3));
        itemRepository.saveAll(List.of(item1, item2, item3));
        bookingRepository.saveAll(List.of(booking1, booking2, booking3, booking4));
    }

    @Test
    void testBookingRepositoryQuery() {
        LocalDateTime testTime = start.plusMinutes(30);
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(user2.getId(), testTime,
                pageable);
        List<Booking> bookingsAll = bookingRepository.findAll();
        assertEquals(4, bookingsAll.size());
        assertEquals(2, bookings.size());
        assertEquals(booking2.getItem().getName(), bookings.get(0).getItem().getName());

        bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(user2.getId(), testTime,
                PageRequest.of(0, 1, BookingRepository.SORT_BY_START_BY_DESC));
        assertEquals(1, bookings.size());
        assertEquals(booking2.getItem().getName(), bookings.get(0).getItem().getName());
        bookings = bookingRepository.findAllByOwnerId(user1.getId(), pageable);
        assertEquals(3, bookings.size());
        assertEquals(booking3.getItem().getName(), bookings.get(0).getItem().getName());
        assertEquals(booking3.getId(), bookings.get(0).getId());
    }
}
