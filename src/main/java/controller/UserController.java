package controller;

import entity.User;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import service.UserService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Valid;

import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class UserController {
    @Inject
    private UserService userService;

    private User user = new User();

    private boolean isRegistration = false; // по умолчанию форма для входа
    public boolean isRegistration() {
        return isRegistration;
    }

    public void setRegistration(boolean isRegistration) {
        this.isRegistration = isRegistration;
    }

    public String createOrLoginUser(){
        if(isRegistration()){
            return createUser();
        } else{
            return loginUser();
        }
    }
    public String loginUser() {
        User authenticated = userService.authenticate(user);

        if (authenticated != null) {
            return "main_page.xhtml";  // Перенаправление после успешного входа
        } else {
            // Добавляем сообщение об ошибке, если аутентификация не удалась
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Incorrect login or password", "Incorrect login or password"));
            return null;  // Оставляем пользователя на той же странице
        }
    }

    public String createUser() {
        // Проверим, существует ли уже пользователь с таким логином
        User existingUser = userService.findByLogin(user.getLogin());

        if (existingUser != null) {
            // Добавляем сообщение об ошибке, если пользователь с таким логином уже существует
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "User with this login already exists", "User with this login already exists"));
            return null;  // Оставляем пользователя на той же странице
        }

        // Если все хорошо, сохраняем нового пользователя
        user.setRole(User.Role.USER);
        userService.save(user);
        return "main_page.xhtml";  // Перенаправление после успешной регистрации
    }

    public List<User> getUsers() {
        return userService.findAll();
    }

}
