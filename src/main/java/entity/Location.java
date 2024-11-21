package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int x;

    @NotNull
    private Float y;

    private int z;

    @Size(min = 1)
    private String name;
}
