package co.edu.uniquindio.application.model;

import co.edu.uniquindio.application.model.enums.State;
import co.edu.uniquindio.application.model.enums.PlaceType;
import co.edu.uniquindio.application.model.enums.Services;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Place {

    @Id
    private String id;

    @Column(nullable = false)
    @Embedded
    private Location location;

    @Column(nullable = false)
    private double price;

    @ElementCollection
    @CollectionTable(
            name = "pics_urls",
            joinColumns = @JoinColumn(name = "place_id")
    )
    @Column(name = "pics_url")
    private List<String> pics_url;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(joinColumns = @JoinColumn(name = "place_id"))
    @Column(name = "amenitie")
    private List<Services> amenities;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false)
    private int totalRatings;


    @Column(nullable = false)
    private double averageRatings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceType placeType;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "place")
    private List<Comment> comments;
}
