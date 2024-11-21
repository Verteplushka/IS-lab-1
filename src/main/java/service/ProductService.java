package service;

import entity.Product;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import util.JPAFactory;

import java.util.List;

@RequestScoped
public class ProductService {
    private final EntityManager entityManager;

    public ProductService() {
        this.entityManager = JPAFactory.getFactory().createEntityManager();
    }
    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    @Transactional
    public void save(Product product) {
        if (product.getId() == null) {
            entityManager.persist(product);
        } else {
            entityManager.merge(product);
        }
    }

    @Transactional
    public void update(Product product) {
        // Проверяем, существует ли объект в базе
        Product existingProduct = findById(product.getId());
        if (existingProduct != null) {
            // Обновляем только те поля, которые были изменены
            entityManager.merge(product);
        } else {
            throw new IllegalArgumentException("Product with id " + product.getId() + " not found");
        }
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        if (product != null) {
            entityManager.remove(product);
        }
    }
}

