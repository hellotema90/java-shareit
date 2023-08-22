package ru.practicum.shareit.request.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "REQUESTS")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@NotBlank(message = "описание не может быть пустым")
    //@Column(name = "DESCRIPTION")
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID")
    private User requestor;
    @Column(name = "CREATED")
    private LocalDateTime created;
    @Transient
    private List<Item> items;
    /*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private User requester;
    @Column(name = "created")
    private LocalDateTime created;
    @Transient
    private List<Item> items;
     */
}
