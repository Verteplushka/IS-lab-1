package bean;

import entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.LazyDataModel;
import service.ProductService;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    private String nameFilter;
    private String partNumberFilter;

    private boolean sortOrderName = true;
    private boolean sortOrderDate = true;
    private boolean sortOrderPrice = true;
    private boolean sortOrderManufactureCost = true;
    private boolean sortOrderRating = true;
    private boolean sortOrderId = true;

    private Comparator<Product> comparator = Comparator.comparing(Product::getId); // Метод сравнения по умолчанию

    private static final Logger logger = Logger.getLogger(ProductBean.class.getName());

    private List<Product> products;


    private List<Product> paginatedProducts; // Продукты на текущей странице
    private int currentPage = 1; // Текущая страница
    private int rowsPerPage = 5; // Количество строк на страницу
    private int totalRows; // Общее количество строк

    public List<Product> getPaginatedProducts() {
        loadProducts(); // Загружаем свежие данные из БД
        return paginatedProducts;
    }


    @PostConstruct
    public void init() {
        loadProducts();
    }

    public void loadProducts() {
        products = productService.findAll().stream()
                .filter(product -> nameFilter == null || nameFilter.isEmpty() || product.getName().equalsIgnoreCase(nameFilter))
                .filter(product -> partNumberFilter == null || partNumberFilter.isEmpty() || product.getPartNumber().equalsIgnoreCase(partNumberFilter))
                .sorted(comparator)
                .collect(Collectors.toList());
        totalRows = products.size();
        updatePaginatedProducts();
    }

    private void updatePaginatedProducts() {
        int fromIndex = (currentPage - 1) * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, totalRows);
        paginatedProducts = products.subList(fromIndex, toIndex);
    }

    public void nextPage() {
        if (currentPage < getTotalPages()) {
            currentPage++;
            updatePaginatedProducts();
        }
    }

    public void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePaginatedProducts();
        }
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalRows / rowsPerPage);
    }

    private void resetSortOrders(String currentSort) {
        if (!"name".equals(currentSort)) {
            sortOrderName = true;
        }
        if (!"date".equals(currentSort)) {
            sortOrderDate = true;
        }
        if (!"price".equals(currentSort)) {
            sortOrderPrice = true;
        }
        if (!"manufactureCost".equals(currentSort)) {
            sortOrderManufactureCost = true;
        }
        if (!"rating".equals(currentSort)) {
            sortOrderRating = true;
        }
        if (!"id".equals(currentSort)) {
            sortOrderId = true;
        }
    }

    public void sortByName() {
        resetSortOrders("name");
        comparator = sortOrderName
                ? Comparator.comparing(Product::getName)
                : Comparator.comparing(Product::getName).reversed();
        sortOrderName = !sortOrderName; // Переключаем порядок сортировки
        loadProducts();
    }

    public void sortByDate() {
        resetSortOrders("date");
        comparator = sortOrderDate
                ? Comparator.comparing(Product::getCreationDate)
                : Comparator.comparing(Product::getCreationDate).reversed();
        sortOrderDate = !sortOrderDate;
        loadProducts();
    }

    public void sortByPrice() {
        resetSortOrders("price");
        comparator = sortOrderPrice
                ? Comparator.comparing(Product::getPrice)
                : Comparator.comparing(Product::getPrice).reversed();
        sortOrderPrice = !sortOrderPrice;
        loadProducts();
    }

    public void sortByManufactureCost() {
        resetSortOrders("manufactureCost");
        comparator = sortOrderManufactureCost
                ? Comparator.comparing(Product::getManufactureCost)
                : Comparator.comparing(Product::getManufactureCost).reversed();
        sortOrderManufactureCost = !sortOrderManufactureCost;
        loadProducts();
    }

    public void sortByRating() {
        resetSortOrders("rating");
        comparator = sortOrderRating
                ? Comparator.comparing(Product::getRating)
                : Comparator.comparing(Product::getRating).reversed();
        sortOrderRating = !sortOrderRating;
        loadProducts();
    }

    public void sortById() {
        resetSortOrders("id");
        comparator = sortOrderId
                ? Comparator.comparing(Product::getId)
                : Comparator.comparing(Product::getId).reversed();
        sortOrderId = !sortOrderId;
        loadProducts();
    }

    public void save() {
//        productService.save(product);
        product = new Product(); // Сброс объекта после сохранения
    }

    public void delete() {
        System.out.println("delete");
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
