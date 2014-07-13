/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.fhjoanneum.swenga.project.audiocracyweb.facades;

import at.fhjoanneum.swenga.project.audiocracyweb.entities.Customer;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 * @author Kevin
 */
@Stateless
public class CustomerFacade extends AbstractFacade<Customer> {

    @PersistenceContext(unitName = "Audiocracy-PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CustomerFacade() {
        super(Customer.class);
    }

    @Override
    public void edit(Customer entity) {
        if (entity.getId() != null) {
            Customer old = super.find(entity.getId());
            if (!old.getPassword().equals(entity.getPassword())) {
                entity.setPassword(DigestUtils.sha256Hex(entity.getPassword()));
            }
        } else {
            entity.setPassword(DigestUtils.sha256Hex(entity.getPassword()));
        }
        super.edit(entity); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void create(Customer entity) {
        entity.setPassword(DigestUtils.sha256Hex(entity.getPassword()));
        super.create(entity);
    }
    /**
     * Finds a customer by providing his email address.
     * @param email
     * @return The customers with the corresponding email address.
     */
    public Customer findByEmail(String email) {
        try {
            return em.createQuery("Select c from Customer c where c.email = :email", Customer.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return new Customer();
        }
    }
}
