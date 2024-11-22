package service;

import entity.*;
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
    public void save(Product product, Coordinates inputCoordinates, Organization inputOrganization, Person inputPerson, Address inputAddress, Location inputLocation, User user) {

        // Сохранение координат
        Coordinates coordinates = findOrCreateCoordinates(inputCoordinates);
        product.setCoordinates(coordinates);

        // Сохранение адреса
        Address officialAddress = findOrCreateAddress(inputAddress);
        inputOrganization.setOfficialAddress(officialAddress);

        // Сохранение организации
        Organization manufacturer = findOrCreateOrganization(inputOrganization);
        product.setManufacturer(manufacturer);

        Location location = findOrCreateLocation(inputLocation);
        inputPerson.setLocation(location);

        // Сохранение владельца
        Person owner = findOrCreatePerson(inputPerson);
        product.setOwner(owner);

        product.setUser(user);
        // Сохранение самого продукта
        if (product.getId() == null) {
            entityManager.persist(product);
        } else {
            entityManager.merge(product);
        }
    }

    @Transactional
    public void update(Product product) {
        Product existingProduct = findById(product.getId());
        if (existingProduct != null) {
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

    public Product findProductById(Long id) {
        return entityManager.find(Product.class, id);
    }

    private Coordinates findOrCreateCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        Coordinates existing = entityManager.createQuery(
                        "SELECT c FROM Coordinates c WHERE c.x = :x AND c.y = :y", Coordinates.class)
                .setParameter("x", coordinates.getX())
                .setParameter("y", coordinates.getY())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing; // Возвращаем существующую запись
        }
        coordinates.setId(null);
        entityManager.persist(coordinates); // Сохраняем новую запись
        return coordinates;
    }

    private Address findOrCreateAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        Address existing = entityManager.createQuery(
                        "SELECT a FROM Address a WHERE a.street = :street AND a.zipCode = :zipCode", Address.class)
                .setParameter("street", address.getStreet())
                .setParameter("zipCode", address.getZipCode())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing; // Возвращаем существующую запись
        }
        entityManager.persist(address); // Сохраняем новую запись
        return address;
    }

    private Organization findOrCreateOrganization(Organization organization) {
        if (organization == null) {
            return null; // Организация может быть null
        }
        Organization existing = entityManager.createQuery(
                        "SELECT o FROM Organization o WHERE o.name = :name AND o.fullName = :fullName", Organization.class)
                .setParameter("name", organization.getName())
                .setParameter("fullName", organization.getFullName())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing; // Возвращаем существующую запись
        }
        entityManager.persist(organization); // Сохраняем новую запись
        return organization;
    }

    private Location findOrCreateLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        Location existing = entityManager.createQuery(
                        "SELECT l FROM Location l WHERE l.x = :x AND l.y = :y AND l.z = :z AND l.name = :name", Location.class)
                .setParameter("x", location.getX())
                .setParameter("y", location.getY())
                .setParameter("z", location.getZ())
                .setParameter("name", location.getName())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing; // Возвращаем существующую запись
        }
        entityManager.persist(location); // Сохраняем новую запись
        return location;
    }

    private Person findOrCreatePerson(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        Person existing = entityManager.createQuery(
                        "SELECT p FROM Person p WHERE p.name = :name AND p.eyeColor = :eyeColor AND p.nationality = :nationality", Person.class)
                .setParameter("name", person.getName())
                .setParameter("eyeColor", person.getEyeColor())
                .setParameter("nationality", person.getNationality())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing; // Возвращаем существующую запись
        }
        entityManager.persist(person); // Сохраняем новую запись
        return person;
    }

}
