package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Organization {
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
    private Address officialAddress;

    @NotNull
    @Positive
    private Float annualTurnover;

    @Positive
    private int employeesCount;

    @Size(max = 1668)
    @NotNull
    @Column(unique = true)
    private String fullName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrganizationType type;
}