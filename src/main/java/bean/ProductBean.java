package bean;

import entity.*;
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

    private Product product = new Product(); // Текущий продукт для создания/редактирования
    private Long idToDelete;

    private Object selectedEntity;
    private Organization manufacturer;
    private Person owner;
    private Address address;
    private Location location;
    private Long manufacturerId;
    private Long ownerId;
    private Long addressId;
    private Long locationId;
    private boolean renderManufacture;
    private boolean renderOwner;


    public List<Product> getProducts() {
        return productService.findAll();
    }

    public void save() {
//        productService.save(product);
        product = new Product(); // Сброс объекта после сохранения
    }

    public void delete() {
        productService.delete(idToDelete, userBean.getUser());
    }

    public void loadManufacturer() {
        renderManufacture = true;
        renderOwner = false;
        manufacturer = productService.findManufacturerById(manufacturerId);
        loadAddress();
    }

    public void loadOwner() {
        renderOwner = true;
        renderManufacture = false;
        owner = productService.findOwnerById(ownerId);
        loadLocation();
    }

    public void loadAddress() {
        address = productService.findAddressById(manufacturer.getOfficialAddress().getId());
    }

    public void loadLocation() {
        location = productService.findLocationById(owner.getLocation().getId());
    }

}
