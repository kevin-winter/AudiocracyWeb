/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.fhjoanneum.swenga.project.audiocracyweb.facades;

import at.fhjoanneum.swenga.project.audiocracyweb.entities.Customer;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Image;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Location;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Kevin
 */
@Stateless
public class LocationFacade extends AbstractFacade<Location> {

    @PersistenceContext(unitName = "Audiocracy-PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LocationFacade() {
        super(Location.class);
    }
    
    public Location findLocationByOfferId(Long id) {
        try {
            return em.createQuery("Select l from Location l inner join l.offers o where o.id = :id", Location.class)
                    .setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return new Location();
        }
    }
    
//    public List<LocationPreview> findPreviews(int start, int length) {
//        return em.createQuery("select new at.fhjoanneum.swenga.project.audiocracyweb.dtos.ProductPreview(p.id,p.name,p.city,p.street,i.id)"
//                + " from Location p left outer join p.images i where i.id = (select min(i2.id) from p.images i2) or i.id is null", LocationPreview.class)
//                .setFirstResult(start)
//                .setMaxResults(length)
//                .getResultList();
//    }
    
     public List<Location> findLocations(String id) {
        return em.createQuery("Select l from Location l where l.CUSTOMERID = :customer", Location.class)
                .setParameter("customer", id).getResultList();
    }

    public Customer findByEmail(String email) {
        //Classname - case-sensitive
        return em.createQuery("Select c from Customer c where c.email = :email", Customer.class)
                .setParameter("email", email).getSingleResult();
    }

    public Image findImageById(Long id) {
        return em.find(Image.class, id);
    }
}
