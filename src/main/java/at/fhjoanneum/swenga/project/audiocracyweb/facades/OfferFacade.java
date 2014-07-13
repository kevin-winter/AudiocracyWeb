/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package at.fhjoanneum.swenga.project.audiocracyweb.facades;

import at.fhjoanneum.swenga.project.audiocracyweb.entities.Offer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Fabian
 */
@Stateless
public class OfferFacade extends AbstractFacade<Offer> {
    @PersistenceContext(unitName = "Audiocracy-PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OfferFacade() {
        super(Offer.class);
    }
    /**
     * Gets all the offers, with date in the future.
     * @return Offers with date in the future.
     */
    public List<Offer> findFutureOffers() {
        try {
            return em.createQuery("Select o from Offer o where o.offerDate >= CURRENT_DATE", Offer.class)
                    .getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
