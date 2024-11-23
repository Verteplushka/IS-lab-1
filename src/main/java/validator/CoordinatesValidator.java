package validator;

import bean.ProductBean;
import controller.ProductController;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.inject.Inject;

@FacesValidator(value = "coordinatesValidator", managed = true) // Регистрация валидатора
public class CoordinatesValidator implements Validator {
    @Inject
    private ProductController productController;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
//        Long coordinatesId = productController.getCoordinatesId();
        int coordinatesX = 0;
        float coordinatesY = 0;

        String coordinatesId = (String) component.getAttributes().get("coordinatesId");


        throw new ValidatorException(new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Validation Error",
                "Please enter either Coordinates ID or both X and Y coordinates. (id=" + coordinatesId + "x=" + coordinatesX + "y=" + coordinatesY + " value=" + value + ")"
        ));
    }
}

