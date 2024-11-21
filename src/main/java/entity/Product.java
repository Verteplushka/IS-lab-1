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
    @Column(name = "creation_date", updatable = false)
    private LocalDate creationDate;

    @Column(name = "unit_of_measure")
    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

    @ManyToOne(cascade = CascadeType.ALL)
    private Organization manufacturer;

    @NotNull
    @Positive
    private Long price;

    @Column(name = "manufacture_cost")
    private int manufactureCost;

    @Positive
    private long rating;

    @Size(min = 25, max = 44)
    @NotEmpty
    @Column(name = "part_number")
    private String partNumber;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Person owner;
}