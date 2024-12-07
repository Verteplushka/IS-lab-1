package service;

import entity.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import util.JPAFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
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
        // Хешируем пароль перед сохранением
        String originalPassword = user.getPassword();
        if (originalPassword != null) {
            user.setPassword(hashPassword(originalPassword));
        }

        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    @Transactional
    public void update(User user) {
        // Хешируем пароль, только если он обновляется
        if (user.getPassword() != null) {
            user.setPassword(hashPassword(user.getPassword()));
        }

        entityManager.merge(user);
    }

    // Удалить пользователя по ID
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            entityManager.remove(user); // Удаляем пользователя
        }
    }

    @Transactional
    public User authenticate(User user) {
        // Проверка, что login и password не пустые
        if (user.getLogin() == null || user.getPassword() == null) {
            return null; // Не аутентифицирован
        }

        // Поиск пользователя по логину
        User foundUser = entityManager.createQuery("SELECT u FROM User u WHERE u.login = :login", User.class)
                .setParameter("login", user.getLogin())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (foundUser == null) {
            return null; // Пользователь не найден
        }

        // Сравниваем хэш введенного пароля с хранимым паролем
        String hashedInputPassword = hashPassword(user.getPassword());
        if (foundUser.getPassword().equals(hashedInputPassword)) {
            return foundUser; // Успешная аутентификация
        } else {
            return null; // Неверный пароль
        }
    }

    @Transactional
    public List<User> getAdminRequests() {
        return entityManager.createQuery("SELECT u FROM User u WHERE u.requestAdminRights = true", User.class)
                .getResultList();
    }

    @Transactional
    public void assignAdminRole(User user) {
        user.setRole(User.Role.ADMIN); // Предполагается, что Role — это Enum
        user.setRequestAdminRights(false); // Удалить запрос после назначения
        entityManager.merge(user); // Обновление в базе данных
    }

    // Метод для хеширования пароля
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-384");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Ошибка алгоритма хеширования", e);
        }
    }

    // Конвертация байтов в шестнадцатеричный вид
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
