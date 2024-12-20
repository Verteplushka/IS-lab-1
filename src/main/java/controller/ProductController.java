package controller;

import bean.ProductBean;
import bean.UserBean;
import entity.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import lombok.Getter;
import lombok.Setter;
import service.ChangeLogService;
import service.ProductService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@SessionScoped
@Getter
@Setter
public class ProductController implements Serializable {

    @Inject
    private ProductService productService;
    @Inject
    private UserBean userBean;
    @Inject
    private ProductBean productBean;

    private Product product = new Product();
    private Coordinates coordinates = new Coordinates();
    private Organization organization = new Organization();
    private Person owner = new Person();
    private Address address = new Address();
    private Location location = new Location();

    private Long coordinatesId;
    private Long organizationId;
    private Long ownerId;
    private Long addressId;
    private Long locationId;

    private Long idToDelete;

    private boolean idModeCoordinates;
    private boolean idModeOrganization;
    private boolean idModeAddress;
    private boolean idModeOwner;
    private boolean idModeLocation;




    public String saveProduct() {
        if(idModeCoordinates){
            coordinates.setId(coordinatesId);
        }
        if(idModeOrganization){
            organization.setId(organizationId);
        }
        if(idModeOwner){
            owner.setId(ownerId);
        }
        if(idModeAddress){
            address.setId(addressId);
        }
        if(idModeLocation){
            location.setId(locationId);
        }
        productService.save(product, coordinates, organization, owner, address, location, userBean.getUser());
        return "main_page.xhtml?faces-redirect=true"; // Перенаправление на страницу с перечнем продуктов
    }

    // Метод для загрузки продукта по ID из URL
    public void loadProduct(Long id) {
        // Загрузка продукта по ID
        product = productService.findProductById(id);

        if (product != null) {
            coordinates = product.getCoordinates(); // Обновляем атрибуты
            organization = product.getManufacturer();
            owner = product.getOwner();
            address = product.getManufacturer().getOfficialAddress();
            location = product.getOwner().getLocation();

            coordinatesId = coordinates.getId();
            organizationId = organization.getId();
            ownerId = owner.getId();
            addressId = address.getId();
            locationId = location.getId();

            product.setCoordinates(null);
            product.setManufacturer(null);
            product.setOwner(null);
            organization.setOfficialAddress(null);
            owner.setLocation(null);
            coordinates.setId(null);
            organization.setId(null);
            owner.setId(null);
            address.setId(null);
            location.setId(null);
        }
    }

    // Метод для извлечения ID из URL и загрузки продукта
    public String loadProductFromRequest() {
        if (idToDelete != null) {
            loadProduct(idToDelete);
        }
        return "product-form.xhtml?faces-redirect=true";
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

    public Long[] getExistingCoordinatesIds(){ return productService.getCoordinatesIds();}

    public Long[] getExistingOrganizationIds(){ return productService.getOrganizationIds();}

    public Long[] getExistingAddressIds(){ return productService.getAddressIds();}

    public Long[] getExistingOwnerIds(){ return productService.getPersonIds();}

    public Long[] getExistingLocationIds(){ return productService.getLocationIds();}




}
