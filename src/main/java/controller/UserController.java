package controller;

import entity.User;
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
    private User user = new User();
    @Inject
    private UserService userService;

    public void createUser() {
        userService.save(user);
        user = new User(); // Сбрасываем форму
    }

    public List<User> getUsers() {
        return userService.findAll();
    }
}
