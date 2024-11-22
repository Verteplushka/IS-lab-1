package bean;

import entity.Product;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init() {
        loadProduct();
    }

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

    public void loadProduct() {
        // Получаем ID из параметров запроса
        String productId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id");
        if (productId != null) {
            this.product = productService.findById(Long.valueOf(productId));
        }
    }

    public String updateProduct() {
        productService.update(product);  // Обновление в базе
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Product updated successfully!"));
        return "product-list.xhtml";  // Перенаправление на страницу списка продуктов
    }

}
