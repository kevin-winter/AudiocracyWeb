package at.fhjoanneum.swenga.project.audiocracyweb.controller;

import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.ImageUtils.MaxType;
import static at.fhjoanneum.swenga.project.audiocracyweb.controller.util.ImageUtils.resizeImage;
import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil;
import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil.PersistAction;
import static at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil.addErrorMessage;
import static at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil.addSuccessMessage;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Image;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Location;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Offer;
import at.fhjoanneum.swenga.project.audiocracyweb.facades.CustomerFacade;
import at.fhjoanneum.swenga.project.audiocracyweb.facades.LocationFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.internal.engine.MessageInterpolatorContext;
import org.primefaces.event.FileUploadEvent;

@Named("locationController")
@SessionScoped
public class LocationController implements Serializable {

    @EJB
    private at.fhjoanneum.swenga.project.audiocracyweb.facades.LocationFacade ejbFacade;
    private List<Location> items = null;
    private Location selected;
    private Long tempLocationID;
    private Offer newOffer;
    @Inject
    private CustomerFacade customerFacade;
    @Inject
    private CustomerController customerController;
    @Inject
    private LocationFacade locationFacade;
    @Inject
    private OfferController offerController;

    public LocationController() {
    }

    public String getLocationsAsJS() {
        String locs = "[";
        int i = 1;
        for (Location loc : getItems()) {
            locs = locs.concat(
                    "['"
                    + loc.getName() + "',"
                    + loc.getLat() + ","
                    + loc.getLng() + ","
                    + loc.getId() + "],");
            i++;
        }
        return locs.substring(0, locs.length() - 1).concat("]");
    }
        
    public Location getLocationByOfferId(Long id) {
        return locationFacade.findLocationByOfferId(id);
    }
    /**
     * Adds an offer to the selected location and resets the offer.
     */
    public void addNewOffer() {
        selected.getOffers().add(newOffer);
        newOffer = null;
    }

    public Offer getNewOffer() {
        return newOffer;
    }

    public void setNewOffer(Offer newOffer) {
        this.newOffer = newOffer;
    }
    
    public void prepareNewOffer(){
        newOffer = new Offer();
    }
    
    public Long getTempLocationID() {
        return tempLocationID;
    }

    public void setTempLocationID(Long tempLocationID) {
        this.tempLocationID = tempLocationID;
    }

    public List<Location> getCustomerLocations(String id) {
        return ejbFacade.findLocations(id);
    }
    /**
     * Handles the file upload by creating a new instance of Image, 
     * resizes it and gives feedback wether it was successful or not.
     * The Image is added to the selected location.
     * @param event 
     */
    public void handleFileUpload(FileUploadEvent event) {
        try {
            Image img = new Image();
            img.setName(event.getFile().getFileName());
            img.setType(event.getFile().getContentType());
            String fileType = img.getType().split("/")[1];
            img.setContent(resizeImage(event.getFile().getInputstream(), fileType, 600, MaxType.MAX_WIDTH));
            img.setThumbnail(resizeImage(event.getFile().getInputstream(), fileType, 80, MaxType.MAX_HEIGHT));
            img.setCreatedAt(new Date());
            getSelected().getImages().add(img);
            addSuccessMessage(JsfUtil.getMessage(FacesContext.getCurrentInstance(), "fileupload_successful", event.getFile().getFileName()));
        } catch (IOException ex) {
            Logger.getLogger(LocationController.class.getName()).log(Level.SEVERE, null, ex);
            addErrorMessage(JsfUtil.getMessage(FacesContext.getCurrentInstance(), "fileupload_error"));
        }
    }

    public Location getSelected() {
        return selected;
    }

    public void setSelected(Location selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LocationFacade getFacade() {
        return ejbFacade;
    }
    /**
     * Provides a new instance of Location and loads the current user.
     * @return Returns a new instance of Location.
     */
    public Location prepareCreate() {
        selected = new Location();
        customerController.loadCurrentUser();
        initializeEmbeddableKey();
        return selected;
    }

    public void init(HttpServletRequest request) {
        Long id = null;
        try {
            id = Long.parseLong(request.getParameter("id"));
        } catch (Exception e) {
        }
        if (id != null) {
            setSelected(getLocation(id));
        }
    }
    /**
     * Selects a location and forwards the user to the Edit Location page.
     * @param location
     * @return navigation string to Edit Location
     */
    public String setEditLocation(Location location) {
        setSelected(location);
        return "editLocation";
    }
    
    /**
     * Removes an offer from a location.
     * @param offer
     * @param location 
     */
     public void deleteOffer(Offer offer, Location location ) {
        selected.getOffers().remove(offer);
        update();
         setSelected(location);
        offerController.setSelected(offer);
        offerController.destroy();
        offerController.prepareCreate();
     }
    

    public void create() {
        //persist(PersistAction.CREATE, JsfUtil.getMessage(FacesContext.getCurrentInstance(), "LocationCreated"));
        customerController.getSelected().getLocations().add(selected);
        customerController.update();
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        selected = null;
        prepareCreate();
    }

    public static boolean isValid(Object obj) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (violations.size() > 0) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            MessageInterpolator mi = factory.getMessageInterpolator();
            for (ConstraintViolation cv : violations) {
                MessageInterpolatorContext mic = new MessageInterpolatorContext(cv.getConstraintDescriptor(), cv.getInvalidValue(), obj.getClass());
                String text = mi.interpolate(cv.getMessageTemplate(), mic, ctx.getViewRoot().getLocale());
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, text, text));
            }
            ctx.validationFailed();
            return false;
        }
        return true;
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessage(FacesContext.getCurrentInstance(), "LocationUpdated"));
        prepareCreate();
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessage(FacesContext.getCurrentInstance(), "LocationDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Location> getItems() {
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
                    JsfUtil.addErrorMessage(JsfUtil.getMessage(FacesContext.getCurrentInstance(), "PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(JsfUtil.getMessage(FacesContext.getCurrentInstance(), "PersistenceErrorOccured"));
            }
        }
    }

    public Location getLocation(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Location> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Location> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Location.class)
    public static class LocationControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LocationController controller = (LocationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "locationController");
            return controller.getLocation(getKey(value));
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
            if (object instanceof Location) {
                Location o = (Location) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Location.class.getName()});
                return null;
            }
        }

    }

}
