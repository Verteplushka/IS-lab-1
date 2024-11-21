package service;

import entity.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import util.JPAFactory;

import java.util.List;

@RequestScoped
public class UserService {

    private final EntityManager entityManager;

    public UserService() {
        this.entityManager = JPAFactory.getFactory().createEntityManager();
    }

    // Получить всех пользователей
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }

    // Найти пользователя по ID
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }

    // Добавить нового пользователя или обновить существующего
    @Transactional
    public void save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    // Удалить пользователя по ID
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            entityManager.remove(user); // Удаляем пользователя
        }
    }
}

