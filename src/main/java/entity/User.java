package entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users_is")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column(unique = true)
    private String login;

    @NotNull
    @NotEmpty
    @Size(max = 10, message = "Password mustn't be longer 10 chars ")  // Ограничение на длину пароля
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN,
        USER
    }

    @Column(name="request_admin_rights", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean requestAdminRights;

}

