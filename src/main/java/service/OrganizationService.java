package service;


import entity.Organization;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import util.JPAFactory;

@RequestScoped

public class OrganizationService {

    private final EntityManager entityManager;

    public OrganizationService() {
        this.entityManager = JPAFactory.getFactory().createEntityManager();
    }
    @Transactional
    public boolean isFullNameTaken(String fullName) {
         return !entityManager.createQuery("SELECT o FROM Organization o WHERE o.fullName = :fullName", Organization.class)
                 .setParameter("fullName", fullName)
                 .getResultList().isEmpty();
    }
}
