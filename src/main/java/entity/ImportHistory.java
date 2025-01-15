package entity;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "import_history")
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @NotNull
    @Column(name = "users_is")
    private String user;

    @Column(name = "objects_added")
    private Integer objectsAdded;  // Количество добавленных объектов (для успешных импортов)

    private LocalDateTime timestamp;  // Время начала операции

    @Override
    public String toString() {
        return "ImportHistory{" +
                "id=" + id +
                ", status='" + status + '\'' +
                ", user='" + user + '\'' +
                ", objectsAdded=" + objectsAdded +
                ", timestamp=" + timestamp +
                '}';
    }


}

