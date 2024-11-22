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

    // Найти пользователя по логину
    public User findByLogin(String login) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                    .setParameter("login", login)
                    .getSingleResult();
        } catch (Exception e) {
            return null;  // Если пользователь не найден, возвращаем null
        }
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

    public User authenticate(User user) {
        // Проверка, что login и password не пустые
        if (user.getLogin() == null || user.getPassword() == null) {
            return null; // Не аутентифицирован
        }

        // Поиск пользователя по логину
        User found_user = entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", user.getLogin())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (found_user == null) {
            return null; // Пользователь не найден
        }

        // Сравниваем введенный пароль с сохраненным (например, предполагаем, что пароли хранятся в зашифрованном виде)
        if (found_user.getPassword().equals(user.getPassword())) {
            return found_user; // Успешная аутентификация
        } else {
            return null; // Неверный пароль
        }
    }
}

