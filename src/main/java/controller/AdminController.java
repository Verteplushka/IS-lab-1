package controller;

import entity.User;
import service.UserService;
import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;

import java.util.List;

@Named
@RequestScoped
@Getter
public class AdminController {

    @Inject
    private UserService userService;

    // Список запросов на админство
    public List<User> getAdminRequests() {
        return userService.getAdminRequests(); // Вернет пользователей с запросами
    }

    // Назначить пользователя администратором
    public String grantAdminRights(User user) {
        userService.assignAdminRole(user); // Устанавливает роль ADMIN
        return "admin-requests.xhtml?faces-redirect=true"; // Обновление страницы
    }
}
