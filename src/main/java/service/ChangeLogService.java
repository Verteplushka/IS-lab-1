package service;

import entity.ChangeLog;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;

@RequestScoped
public class ChangeLogService {

    @PersistenceContext
    private EntityManager entityManager;

    public void logProductChange(Long entityId, String changeType, Long userId) {
        ChangeLog logEntry = new ChangeLog(
                LocalDateTime.now(),
                changeType,
                entityId,
                "product",
                userId
        );
        entityManager.persist(logEntry);
    }
}
