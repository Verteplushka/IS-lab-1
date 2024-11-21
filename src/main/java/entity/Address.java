package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String street;

    @NotNull
    private String zipCode;
}
