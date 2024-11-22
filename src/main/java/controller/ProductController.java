package controller;

import bean.UserBean;
import entity.*;
import lombok.Getter;
import lombok.Setter;
import service.ProductService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
@Getter
@Setter
public class ProductController {

    @Inject
    private ProductService productService;
    @Inject
    private UserBean userBean;

    private Product product = new Product();
    private Coordinates coordinates = new Coordinates();
    private Organization organization = new Organization();
    private Person owner = new Person();
    private Address address = new Address();
    private Location location = new Location();
    public String saveProduct() {
        productService.save(product, coordinates, organization, owner, address, location, userBean.getUser());
        return "main_page.xhtml?faces-redirect=true"; // Перенаправление на страницу с перечнем продуктов
    }

    // Динамические списки для enum
    public UnitOfMeasure[] getUnitOfMeasureValues() {
        return UnitOfMeasure.values();
    }

    public OrganizationType[] getOrganizationTypeValues() {
        return OrganizationType.values();
    }

    public Color[] getColorValues() {
        return Color.values();
    }

    public Country[] getCountryValues() {
        return Country.values();
    }
}

