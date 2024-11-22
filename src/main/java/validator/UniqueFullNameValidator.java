package validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;
import service.OrganizationService;

@FacesValidator("uniqueFullNameValidator") // Регистрация валидатора
public class UniqueFullNameValidator implements Validator {

    @Inject
    private OrganizationService organizationService;  // Сервис для проверки уникальности

    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
        String fullName = (String) value;
        if (organizationService.isFullNameTaken(fullName)) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Full Name is already taken", null);
            throw new ValidatorException(message);
        }
    }
}

