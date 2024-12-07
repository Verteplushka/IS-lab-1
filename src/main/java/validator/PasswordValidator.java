package validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesValidator(value = "passwordValidator", managed = true) //

public class PasswordValidator implements Validator {
    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        String password = (String) value;

        // Ваша валидация длины пароля
        if (password.length() > 10) {
            FacesMessage message = new FacesMessage("Password mustn't be longer 10 chars ");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }

        // Дополнительная логика валидации, например, сложность пароля
    }
}

