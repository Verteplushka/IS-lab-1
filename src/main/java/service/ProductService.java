package service;

import entity.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import util.JPAFactory;
import util.WebSocketEndpoint;

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
        // Сохранение данных
        Coordinates coordinates = findOrCreateCoordinates(inputCoordinates);
        product.setCoordinates(coordinates);

        Address officialAddress = findOrCreateAddress(inputAddress);
        inputOrganization.setOfficialAddress(officialAddress);

        Organization manufacturer = findOrCreateOrganization(inputOrganization);
        product.setManufacturer(manufacturer);

        Location location = findOrCreateLocation(inputLocation);
        inputPerson.setLocation(location);

        Person owner = findOrCreatePerson(inputPerson);
        product.setOwner(owner);

        product.setUser(user);

        // Сохранение продукта
        if (product.getId() == null) {
            entityManager.persist(product);
        } else {
            entityManager.merge(product);
        }

        // Отправка обновлений через WebSocket
        WebSocketEndpoint.sendUpdateToAllClients("Product added/updated: " + product.getName());
    }

    @Transactional
    public void update(Product product) {
        Product existingProduct = findById(product.getId());
        if (existingProduct != null) {
            entityManager.merge(product);
            // Отправка уведомления об изменении
            WebSocketEndpoint.sendUpdateToAllClients("Product updated: " + product.getName());
        } else {
            throw new IllegalArgumentException("Product with id " + product.getId() + " not found");
        }
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        if (product != null) {
            entityManager.remove(product);
            // Отправка уведомления об удалении
            WebSocketEndpoint.sendUpdateToAllClients("Product deleted: " + product.getName());
        }
    }

    public Product findProductById(Long id) {
        return entityManager.find(Product.class, id);
    }

    private Coordinates findOrCreateCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        if(coordinates.getId() != null){
            return entityManager.find(Coordinates.class, coordinates.getId());
        }

        Coordinates existing = entityManager.createQuery("SELECT c FROM Coordinates c WHERE c.x = :x AND c.y = :y", Coordinates.class).setParameter("x", coordinates.getX()).setParameter("y", coordinates.getY()).getResultStream().findFirst().orElse(null);

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

        if(address.getId() != null){
            return entityManager.find(Address.class, address.getId());
        }

        Address existing = entityManager.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zipCode = :zipCode", Address.class).setParameter("street", address.getStreet()).setParameter("zipCode", address.getZipCode()).getResultStream().findFirst().orElse(null);

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

        if(organization.getId() != null){
            return entityManager.find(Organization.class, organization.getId());
        }

        Organization existing = entityManager.createQuery("SELECT o FROM Organization o WHERE o.name = :name AND o.fullName = :fullName", Organization.class).setParameter("name", organization.getName()).setParameter("fullName", organization.getFullName()).getResultStream().findFirst().orElse(null);

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

        if(location.getId() != null){
            return entityManager.find(Location.class, location.getId());
        }

        Location existing = entityManager.createQuery("SELECT l FROM Location l WHERE l.x = :x AND l.y = :y AND l.z = :z AND l.name = :name", Location.class).setParameter("x", location.getX()).setParameter("y", location.getY()).setParameter("z", location.getZ()).setParameter("name", location.getName()).getResultStream().findFirst().orElse(null);

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

        if(person.getId() != null){
            return entityManager.find(Person.class, person.getId());
        }

        Person existing = entityManager.createQuery("SELECT p FROM Person p WHERE p.name = :name AND p.eyeColor = :eyeColor AND p.nationality = :nationality", Person.class).setParameter("name", person.getName()).setParameter("eyeColor", person.getEyeColor()).setParameter("nationality", person.getNationality()).getResultStream().findFirst().orElse(null);

        if (existing != null) {
            return existing; // Возвращаем существующую запись
        }
        entityManager.persist(person); // Сохраняем новую запись
        return person;
    }

    public Organization findManufacturerById(Long manufacturerId) {
        Organization manufacturer = entityManager.find(Organization.class, manufacturerId);
        if (manufacturer == null) {
            throw new IllegalArgumentException(
                    "Manufacturer with ID " + manufacturerId + " not found.");
        }
        return manufacturer;
    }

    public Person findOwnerById(Long ownerId) {
        Person owner = entityManager.find(Person.class, ownerId);
        if (owner == null) {
            throw new IllegalArgumentException(
                    "Owner with ID " + ownerId + " not found.");
        }
        return owner;
    }

    public Address findAddressById(Long addressId) {
        Address address = entityManager.find(Address.class, addressId);
        if (address == null) {
            throw new IllegalArgumentException(
                    "Owner with ID " + addressId + " not found.");
        }
        return address;
    }

    public Location findLocationById(Long locationId) {
        Location location = entityManager.find(Location.class, locationId);
        if (location == null) {
            throw new IllegalArgumentException(
                    "Owner with ID " + location + " not found.");
        }
        return location;
    }

    @Transactional
    public Product getProductWithMinName() {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY LENGTH(p.name) ASC", Product.class).setMaxResults(1).getSingleResult();
    }

    @Transactional
    public Long countByPartNumber(String partNumber) {
        return entityManager.createQuery("SELECT COUNT(p) FROM Product p WHERE p.partNumber = :partNumber", Long.class).setParameter("partNumber", partNumber).getSingleResult();
    }

    @Transactional
    public List<Product> getProductsByPartNumberSubstring(String substring) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.partNumber LIKE :substring", Product.class).setParameter("substring", "%" + substring + "%").getResultList();
    }

    @Transactional
    public List<Product> getProductsByPriceRange(Long minPrice, Long maxPrice) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice", Product.class).setParameter("minPrice", minPrice).setParameter("maxPrice", maxPrice).getResultList();
    }

    @Transactional
    public void increasePrices(Double percentage) {
        entityManager.createQuery(
                        "UPDATE Product p SET p.price = FUNCTION('ROUND', p.price * (1 + :percentage / 100.0))")
                .setParameter("percentage", percentage)
                .executeUpdate();
    }
}
