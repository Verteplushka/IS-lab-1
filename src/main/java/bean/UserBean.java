package bean;

import controller.ProductController;
import entity.User;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Named
@SessionScoped
@Getter
@Setter
public class UserBean implements Serializable {

    private User user;

    public boolean isLoggedIn() {
        return user != null;
    }

    public String loginRedirect() {
        if (isLoggedIn()) {
            logout();
            return null;
        } else {
            return "user-form.xhtml?faces-redirect=true"; // Перенаправление на страницу входа
        }
    }

    // Метод выхода из аккаунта
    public void logout() {
        // Очистка данных сессии
        user = null;
    }

    // Метод запроса прав администратора
    public String requestAdminRights() {
        // Здесь можно реализовать логику отправки запроса на сервер
        // Например, отправка уведомления в систему
        if (user != null) {
            System.out.println("Запрос прав администратора отправлен для пользователя: " + user.getId());
        }
        return null; // Остаёмся на текущей странице
    }

}
