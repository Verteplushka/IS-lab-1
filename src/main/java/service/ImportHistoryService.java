package service;

import entity.ImportHistory;
import jakarta.enterprise.context.RequestScoped;
import jakarta.transaction.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@RequestScoped
public class ImportHistoryService implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;

    // Сохранение записи в историю импорта
    @Transactional
    public ImportHistory save(ImportHistory history) {
        entityManager.persist(history);
        return history;
    }

    @Transactional
    public void update(ImportHistory history) {
        entityManager.merge(history);
    }

    // Получение истории импорта для текущего пользователя
    public List<ImportHistory> getImportHistoryForUser(String username) {
        TypedQuery<ImportHistory> query = entityManager.createQuery(
                "SELECT h FROM ImportHistory h WHERE h.user = :user ORDER BY h.timestamp DESC", ImportHistory.class);
        query.setParameter("user", username);
        return query.getResultList();
    }

    // Получение всей истории импорта (для администратора)
    public List<ImportHistory> getAllImportHistory() {
        TypedQuery<ImportHistory> query = entityManager.createQuery(
                "SELECT h FROM ImportHistory h ORDER BY h.timestamp DESC", ImportHistory.class);
        return query.getResultList();
    }
}
