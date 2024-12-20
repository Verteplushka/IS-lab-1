package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="organization")
public class Organization {
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
    @JoinColumn(name = "official_address_id")
    private Address officialAddress;

    @NotNull
    @Positive(message = "Annual Turnover must be positive")
    @Column(name = "annual_turnover")
    private Float annualTurnover;

    @Positive(message = "Employees Count must be positive")
    @Column(name = "employees_count")
    private int employeesCount;

    @Size(max = 1668, message = "Full Name's length mustn't be greater 1668")
    @NotNull
    @Column(name = "full_name", unique = true)
    private String fullName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrganizationType type;
}