package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.AccessException;
import ru.practicum.shareit.exeption.InternalServerError;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("пользователь с id %d не найден", userId)));
    }

    private Item getItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("вещь с id %d не найдена", itemId)));
    }

    @Transactional
    public OutputBookingDto addBooking(InputBookingDto bookingDto, Long userId) {
        getUserById(userId);
        Long itemId = bookingDto.getItemId();
        Item item = getItemById(itemId);
        User owner = item.getOwner();
        if (owner == null) {
            throw new AccessException(String.format("вещь с id = %d не имеет владельца.", itemId));
        }
        if (owner.getId().equals(userId)) {
            throw new AccessException(String.format("Booker не может быть владельцем вещи id: %d", userId));
        }
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (end.isBefore(start) || end.equals(start)) {
            throw new ValidationException(String.format("неверное время начала бронирования = %s и конца = %s", start, end));
        }
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("вещь с id: %d не доступна", userId));
        }
        Booking booking = Booking.builder()
                .start(start)
                .end(end)
                .item(item)
                .booker(getUserById(userId))
                .status(BookingStatus.WAITING)
                .build();
        return BookingMapper.toBookingDtoRequest(bookingRepository.save(booking));
    }

    @Transactional
    public OutputBookingDto approveBooking(Long bookingId, Long userId, Boolean approve) {
        getUserById(userId);
        Booking booking = getBookingById(bookingId, userId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException(String.format("Booking with id: %d already have status %s",
                    bookingId, BookingStatus.APPROVED));
        }
        if (!userId.equals(getItemOwnerId(booking))) {
            throw new AccessException(String.format("Access to User id:%s for booking id:%s is denied",
                    userId, booking.getId()));
        }
        BookingStatus bookingStatus = approve ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(bookingStatus);
        return BookingMapper.toBookingDtoRequest(bookingRepository.save(booking));
    }

    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("бронирование с id: %d не найдено", bookingId)));
        return booking;
    }

    private Long getItemOwnerId(Booking booking) {
        User booker = booking.getBooker();
        if (booker == null) {
            throw new InternalServerError(String.format("Booking with id: %s Bouker is not installed!", booker.getId()));
        }
        Long bookerId = booker.getId();
        Item item = booking.getItem();
        if (item == null) {
            throw new InternalServerError(String.format("Booking with id: %s Item is not installed!", booker.getId()));
        }
        User itemOwner = item.getOwner();
        if (itemOwner == null) {
            throw new InternalServerError(String.format("Booking with id: %s Owner is not installed!", booker.getId()));
        }
        return itemOwner.getId();
    }

    public OutputBookingDto getBookingDtoById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId, userId);
        getUserById(userId);
        Long itemOwnerId = getItemOwnerId(booking);
        Long bookerId = booking.getBooker().getId();
        if (!((bookerId.equals(userId)) || (itemOwnerId.equals(userId)))) {
            throw new AccessException(String.format("Access to User id:%s for booking id:%s is denied",
                    userId, booking.getId()));
        }
        return BookingMapper.toBookingDtoRequest(booking);
    }

    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsOfBooker(State state, Long bookerId, int from, int size) {
        getUserById(bookerId);
        Pageable pageable = PageRequest.of(size == 0 ? 0 : from / size, size, BookingRepository.SORT_BY_START_BY_DESC);
        switch (state) {
            case WAITING:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, pageable));
            case PAST:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), pageable));
            case FUTURE:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), pageable));
            case CURRENT:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(),
                                pageable));
            case ALL:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerId(bookerId, pageable));
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsOfOwner(State state, Long ownerId, int from, int size) {
        getUserById(ownerId);
        Pageable pageable = PageRequest.of(size == 0 ? 0 : from / size, size, BookingRepository.SORT_BY_START_BY_DESC);
        switch (state) {
            case WAITING:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable));
            case REJECTED:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable));
            case PAST:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), pageable));
            case FUTURE:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), pageable));
            case CURRENT:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now(),
                                pageable));
            case ALL:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerId(ownerId, pageable));
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
