package service;

import entity.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import util.JPAFactory;
import util.WebSocketEndpoint;

import java.util.List;

@RequestScoped
public class ProductService {
    private final EntityManager entityManager;

    @Inject
    ChangeLogService changeLogService;

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
            changeLogService.logProductChange(product.getId(), "SAVE", user.getId());
        } else {
            entityManager.merge(product);
            changeLogService.logProductChange(product.getId(), "UPDATE", user.getId());
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
    public void delete(Long id, User user) {
        Product product = findById(id);
        if (product != null) {
            entityManager.remove(product);
            // Отправка уведомления об удалении
            WebSocketEndpoint.sendUpdateToAllClients("Product deleted: " + product.getName());
        }

        changeLogService.logProductChange(id, "DELETE", user.getId());
    }

    public Product findProductById(Long id) {
        return entityManager.find(Product.class, id);
    }

    private Coordinates findOrCreateCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }

        if (coordinates.getId() != null) {
            return entityManager.find(Coordinates.class, coordinates.getId());
        }

        Coordinates existing = entityManager.createQuery("SELECT c FROM Coordinates c WHERE c.x = :x " +
                        "AND c.y = :y", Coordinates.class)
                .setParameter("x", coordinates.getX())
                .setParameter("y", coordinates.getY())
                .getResultStream().findFirst().orElse(null);

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

        if (address.getId() != null) {
            return entityManager.find(Address.class, address.getId());
        }

        Address existing = entityManager.createQuery("SELECT a FROM Address a WHERE a.street = :street " +
                        "AND a.zipCode = :zipCode", Address.class)
                .setParameter("street", address.getStreet())
                .setParameter("zipCode", address.getZipCode())
                .getResultStream().findFirst().orElse(null);

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

        if (organization.getId() != null) {
            return entityManager.find(Organization.class, organization.getId());
        }

        Organization existing = entityManager.createQuery("SELECT o FROM Organization o WHERE o.name = :name " +
                        "AND o.annualTurnover = :annualTurnover AND o.employeesCount = :employeesCount AND o.type = :type " +
                        "AND o.fullName = :fullName AND o.officialAddress = :officialAddress", Organization.class)
                .setParameter("name", organization.getName())
                .setParameter("officialAddress", organization.getOfficialAddress())
                .setParameter("annualTurnover", organization.getAnnualTurnover())
                .setParameter("employeesCount", organization.getEmployeesCount())
                .setParameter("fullName", organization.getFullName())
                .setParameter("type", organization.getType())
                .getResultStream().findFirst().orElse(null);

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

        if (location.getId() != null) {
            return entityManager.find(Location.class, location.getId());
        }

        Location existing = entityManager.createQuery("SELECT l FROM Location l WHERE l.x = :x AND l.y = :y " +
                        "AND l.z = :z AND l.name = :name", Location.class)
                .setParameter("x", location.getX())
                .setParameter("y", location.getY())
                .setParameter("z", location.getZ())
                .setParameter("name", location.getName())
                .getResultStream().findFirst().orElse(null);

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

        if (person.getId() != null) {
            return entityManager.find(Person.class, person.getId());
        }

        Person existing = entityManager.createQuery("SELECT p FROM Person p WHERE p.name = :name " +
                        "AND p.eyeColor = :eyeColor AND p.hairColor = :hairColor AND p.location = :location " +
                        "AND p.weight = :weight AND p.nationality = :nationality", Person.class)
                .setParameter("name", person.getName())
                .setParameter("eyeColor", person.getEyeColor())
                .setParameter("hairColor", person.getHairColor())
                .setParameter("location", person.getLocation())
                .setParameter("weight", person.getWeight())
                .setParameter("nationality", person.getNationality())
                .getResultStream().findFirst().orElse(null);

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
    public void increasePrices(Double percentage, User user) {
        entityManager.createQuery(
                        "UPDATE Product p " +
                                "SET p.price = FUNCTION('ROUND', p.price * (1 + :percentage / 100.0)) " +
                                "WHERE p.user = :user")
                .setParameter("percentage", percentage)
                .setParameter("user", user)
                .executeUpdate();
    }

    @Transactional
    public Long[] getCoordinatesIds() {
        List<Long> ids = entityManager.createQuery("SELECT c.id FROM Coordinates c", Long.class)
                .getResultList();

        return ids.toArray(new Long[0]);
    }

    @Transactional
    public Long[] getOrganizationIds() {
        List<Long> ids = entityManager.createQuery("SELECT o.id FROM Organization o", Long.class)
                .getResultList();

        return ids.toArray(new Long[0]);
    }

    @Transactional
    public Long[] getAddressIds() {
        List<Long> ids = entityManager.createQuery("SELECT a.id FROM Address a", Long.class)
                .getResultList();

        return ids.toArray(new Long[0]);
    }

    @Transactional
    public Long[] getPersonIds() {
        List<Long> ids = entityManager.createQuery("SELECT p.id FROM Person p", Long.class)
                .getResultList();

        return ids.toArray(new Long[0]);
    }

    @Transactional
    public Long[] getLocationIds() {
        List<Long> ids = entityManager.createQuery("SELECT l.id FROM Location l", Long.class)
                .getResultList();

        return ids.toArray(new Long[0]);
    }

    @Transactional
    public boolean checkUniqueFullName(String fullName) {
        Long count = entityManager.createQuery("SELECT COUNT(o) FROM Organization o WHERE o.fullName = :fullName", Long.class)
                .setParameter("fullName", fullName)
                .getSingleResult();
        return count == 0;
    }

    public Long findOrganizationId(Organization organization) {
        if (organization == null) {
            return null;
        }

        Address existing = new Address();

        if (organization.getOfficialAddress().getId() != null) {
            existing = entityManager.find(Address.class, organization.getOfficialAddress().getId());
        } else{
            existing = entityManager.createQuery("SELECT a FROM Address a WHERE a.street = :street " +
                            "AND a.zipCode = :zipCode", Address.class)
                    .setParameter("street", organization.getOfficialAddress().getStreet())
                    .setParameter("zipCode", organization.getOfficialAddress().getZipCode())
                    .getResultStream().findFirst().orElse(null);
        }

        if(existing == null){
            return null;
        }

        organization.setOfficialAddress(existing);

        return entityManager.createQuery("SELECT o.id FROM Organization o WHERE o.name = :name " +
                        "AND o.annualTurnover = :annualTurnover AND o.employeesCount = :employeesCount AND o.type = :type " +
                        "AND o.fullName = :fullName AND o.officialAddress = :officialAddress", Long.class)
                .setParameter("name", organization.getName())
                .setParameter("officialAddress", organization.getOfficialAddress())
                .setParameter("annualTurnover", organization.getAnnualTurnover())
                .setParameter("employeesCount", organization.getEmployeesCount())
                .setParameter("fullName", organization.getFullName())
                .setParameter("type", organization.getType())
                .getResultStream()
                .findFirst()
                .orElse(null); // Возвращаем id, если запись найдена, иначе null


    }
}
