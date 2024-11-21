package controller;

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

    private Product product = new Product();
    public String saveProduct() {
        productService.save(product);
        return "product-list.xhtml"; // Перенаправление на страницу с перечнем продуктов
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

