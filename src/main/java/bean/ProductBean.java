package bean;

import entity.Product;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import service.ProductService;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
@Getter
@Setter
public class ProductBean implements Serializable {
    @Inject
    private ProductService productService;


    private Product product = new Product(); // Текущий продукт для создания/редактирования
    private Long idToDelete;

    public List<Product> getProducts() {
        return productService.findAll();
    }

    public void save() {
//        productService.save(product);
        product = new Product(); // Сброс объекта после сохранения
    }

    public void delete() {
        productService.delete(idToDelete);
    }

}
