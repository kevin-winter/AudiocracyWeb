package at.fhjoanneum.swenga.project.audiocracyweb.controller;

import at.fhjoanneum.swenga.project.audiocracyweb.entities.Offer;
import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil;
import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil.PersistAction;
import at.fhjoanneum.swenga.project.audiocracyweb.facades.OfferFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@Named("offerController")
@SessionScoped
public class OfferController implements Serializable {

    @EJB
    private at.fhjoanneum.swenga.project.audiocracyweb.facades.OfferFacade ejbFacade;
    private List<Offer> items = null;
    private Offer selected = null;
    @Inject
    private OfferFacade offerFacade;

    public OfferController() {
    }

    public Offer getSelected() {
        return selected;
    }

    public void setSelected(Offer selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private OfferFacade getFacade() {
        return ejbFacade;
    }

    public Offer prepareCreate() {
        selected = new Offer();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        //persist(PersistAction.CREATE, ResourceBundle.getBundle("/locales/Bundle").getString("OfferCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        //persist(PersistAction.UPDATE, ResourceBundle.getBundle("/locales/Bundle").getString("OfferUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/locales/Bundle").getString("OfferDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    /**
     * Gets all offers with date in the future.
     * @return 
     */
    public List<Offer> getFutureOffers() {
        return offerFacade.findFutureOffers();
    }

    public List<Offer> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/locales/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/locales/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Offer getOffer(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Offer> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Offer> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Offer.class)
    public static class OfferControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            OfferController controller = (OfferController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "offerController");
            return controller.getOffer(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Offer) {
                Offer o = (Offer) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Offer.class.getName()});
                return null;
            }
        }

    }

}
