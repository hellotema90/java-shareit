package ru.practicum.shareit.booking.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "BOOKINGS")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "START_BOOKING")
    private LocalDateTime start;
    @Column(name = "END_BOOKING")
    private LocalDateTime end;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID")
    private Item item;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BOOKER_ID")
    private User booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
