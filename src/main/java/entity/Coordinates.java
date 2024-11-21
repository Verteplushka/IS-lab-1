package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer x;

    @Max(327)
    private float y;
}
