package ru.practicum.shareit.item.comment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph(
        name = "comment-entity-graph",
        attributeNodes = {@NamedAttributeNode("author")
        }
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;
    @Column(name = "created_time")
    @CreationTimestamp
    private LocalDateTime created;
}