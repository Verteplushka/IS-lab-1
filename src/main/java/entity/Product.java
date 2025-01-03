package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @NotNull
    @Positive
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Coordinates coordinates;

    @NotNull
    @Column(name = "creation_date", updatable = false)
    private LocalDate creationDate;

    @PrePersist
    private void setCreationDate() {
        this.creationDate = LocalDate.now();
    }

    @Column(name = "unit_of_measure")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    @ManyToOne(cascade = CascadeType.ALL)
    private Organization manufacturer;

    @NotNull
    @Positive(message = "Price must be positive")
    private Long price;

    @Column(name = "manufacture_cost")
    private int manufactureCost;

    @Positive(message = "Rating must be positive")
    private long rating;

    @Size(min = 25, max = 44, message = "Part Number's length must be between 25 and 44")
    @NotEmpty
    @Column(name = "part_number")
    private String partNumber;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Person owner;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}