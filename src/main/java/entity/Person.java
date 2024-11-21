package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Color eyeColor;

    @Enumerated(EnumType.STRING)
    private Color hairColor;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    private Location location;

    @NotNull
    @Positive
    private Double weight;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Country nationality;
}