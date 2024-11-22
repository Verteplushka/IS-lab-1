package bean;

import controller.ProductController;
import entity.User;
import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import service.UserService;

import java.io.Serializable;

@Named
@SessionScoped
@Getter
@Setter
public class UserBean implements Serializable {
    @Inject
    private UserService userService;

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
        if (user != null) {
            try {
                // Установить поле requestAdminRights в true для текущего пользователя
                user.setRequestAdminRights(true);

                // Сохранить изменения через UserService
                userService.update(user);

                // Вывести подтверждение в консоль (или заменить логированием)
                System.out.println("Запрос прав администратора отправлен для пользователя: " + user.getId());

                // Возврат сообщения для отображения в интерфейсе (опционально)
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Запрос на права администратора отправлен", null));
            } catch (Exception e) {
                // Обработка ошибок
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при отправке запроса на права администратора", null));
                e.printStackTrace();
            }
        } else {
            // Обработка случая, если пользователь не авторизован
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Вы должны быть авторизованы, чтобы запросить права администратора", null));
        }
        return null; // Остаёмся на текущей странице
    }


}
