package bean;

import entity.Product;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import service.ProductService;

import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class OperationsBean {

    private String partNumber;
    private String substring;
    private Long minPrice;
    private Long maxPrice;
    private Double percentage;

    private Product minNameObject;
    private Long countByPartNumber;
    private List<Product> objectsByPartNumberSubstring;
    private List<Product> objectsByPriceRange;
    private boolean priceUpdated;

    @Inject
    private ProductService productService;

    public Product getMinNameObject() {
        minNameObject = productService.getProductWithMinName();
        return minNameObject;
    }

    public Long countObjectsByPartNumber() {
        countByPartNumber = productService.countByPartNumber(partNumber);
        return countByPartNumber;
    }

    public List<Product> getObjectsByPartNumberSubstring() {
        objectsByPartNumberSubstring = productService.getProductsByPartNumberSubstring(substring);
        return objectsByPartNumberSubstring;
    }

    public List<Product> getObjectsByPriceRange() {
        objectsByPriceRange = productService.getProductsByPriceRange(minPrice, maxPrice);
        return objectsByPriceRange;
    }

    public void increasePriceByPercentage() {
        productService.increasePrices(percentage);
        priceUpdated = true;
    }
}

