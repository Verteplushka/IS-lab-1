package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Positive
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Coordinates coordinates;

    @NotNull
    @Column(updatable = false)
    private LocalDate creationDate;

    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    @ManyToOne(cascade = CascadeType.ALL)
    private Organization manufacturer;

    @NotNull
    @Positive
    private Long price;

    private int manufactureCost;

    @Positive
    private long rating;

    @Size(min = 25, max = 44)
    @NotEmpty
    private String partNumber;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Person owner;
}