package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.*;
import ru.practicum.shareit.exeption.IllegalArgumentException;
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
        Booking booking = getBookingById(bookingId, userId);
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new ValidationException(String.format(" у бронирования с id: %d уже есть статус %s",
                    bookingId, BookingStatus.APPROVED));
        }
        checkAccessToBooking(booking, userId, false);
        BookingStatus bookingStatus = approve ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(approve ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.updateStatus(bookingStatus, bookingId);
        return BookingMapper.toBookingDtoRequest(bookingRepository.save(booking));
    }

    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("бронирование с id: %d не найдено", bookingId)));
        checkAccessToBooking(booking, userId, true);
        return booking;
    }

    private void checkAccessToBooking(Booking booking, Long userId, boolean accessForBooker) {
        User booker = booking.getBooker();
        if (booker == null) {
            throw new InternalServerError(String.format("бронирование с id: %s Booker не установлен", booker.getId()));
        }
        Long bookerId = booker.getId();
        Item item = booking.getItem();
        if (item == null) {
            throw new InternalServerError(String.format("бронирование с id: %s вещь не установлена", booker.getId()));
        }
        User owner = item.getOwner();
        if (owner == null) {
            throw new InternalServerError(String.format("бронирование с id: %s владелец не установлен", booker.getId()));
        }
        Long ownerId = owner.getId();
        if (ownerId.equals(userId)) {
            return;
        }
        if (accessForBooker && bookerId.equals(userId)) {
            return;
        }
        throw new AccessException(String.format("доступ к пользователю с id:%s для бронирования id:%s запрещен",
                userId, booking.getId()));
    }

    public OutputBookingDto getBookingDtoById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId, userId);
        return BookingMapper.toBookingDtoRequest(booking);
    }

    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsOfBooker(State state, Long bookerId) {
        getUserById(bookerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case WAITING:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sort));
            case REJECTED:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sort));
            case PAST:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), sort));
            case FUTURE:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), sort));
            case CURRENT:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now()));
            case ALL:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByBookerId(bookerId, sort));
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Transactional(readOnly = true)
    public List<OutputBookingDto> getBookingsOfOwner(State state, Long ownerId) {
        getUserById(ownerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "start");

        switch (state) {
            case WAITING:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.WAITING));
            case REJECTED:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED));
            case PAST:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndEndBefore(ownerId, LocalDateTime.now()));
            case FUTURE:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStartAfter(ownerId, LocalDateTime.now()));
            case CURRENT:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerIdAndStartBeforeAndEndAfter(ownerId, LocalDateTime.now()));
            case ALL:
                return BookingMapper.toBookingDtoRequestsList(
                        bookingRepository.findAllByOwnerId(ownerId, sort));
            default:
                throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
