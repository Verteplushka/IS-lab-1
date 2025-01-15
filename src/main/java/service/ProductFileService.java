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
public class ProductFileService {
    private final EntityManager entityManager;

    @Inject
    ChangeLogService changeLogService;

    public ProductFileService() {
        this.entityManager = JPAFactory.getFactory().createEntityManager();
    }

    public List<Product> findAll() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public Product findById(Long id) {
        return entityManager.find(Product.class, id);
    }

    @Transactional
    public void save(List<Product> products, User user) {
        for(Product product : products){
            // Сохранение данных
            Coordinates coordinates = findOrCreateCoordinates(product.getCoordinates());
            product.setCoordinates(coordinates);

            Address officialAddress = findOrCreateAddress(product.getManufacturer().getOfficialAddress());
            product.getManufacturer().setOfficialAddress(officialAddress);

            Organization manufacturer = findOrCreateOrganization(product.getManufacturer());
            product.setManufacturer(manufacturer);

            Location location = findOrCreateLocation(product.getOwner().getLocation());
            product.getOwner().setLocation(location);

            Person owner = findOrCreatePerson(product.getOwner());
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
}
