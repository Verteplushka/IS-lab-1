package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "change_log")
public class ChangeLog {

    // Геттеры и сеттеры
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Column(name = "change_type", nullable = false)
    private String changeType; // Тип изменения: "CREATE", "UPDATE", "DELETE"

    @Column(name = "entity_id", nullable = false)
    private Long entityId; // ID измененной сущности

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "user_id", nullable = false)
    private Long userId; // Автор изменения

    // Конструкторы
    public ChangeLog() {}

    public ChangeLog(LocalDateTime changeDate, String changeType, Long entityId, String tableName, Long userId) {
        this.changeDate = changeDate;
        this.changeType = changeType;
        this.entityId = entityId;
        this.tableName = tableName;
        this.userId = userId;
    }

}

