package co.edu.uniquindio.application.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment {

    @Id
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private int rating;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Place place;

    @ManyToOne(optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;


    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = UUID.randomUUID().toString();
        }

        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}