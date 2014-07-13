package at.fhjoanneum.swenga.project.audiocracyweb.controller;

import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil;
import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil.PersistAction;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Customer;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Location;
import at.fhjoanneum.swenga.project.audiocracyweb.facades.CustomerFacade;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

@Named("customerController")
@SessionScoped
public class CustomerController implements Serializable {

    @EJB
    private at.fhjoanneum.swenga.project.audiocracyweb.facades.CustomerFacade ejbFacade;
    private List<Customer> items = null;
    private Customer selected;
    @Inject
    private CustomerFacade customerFacade;
    @Inject
    private LocationController locationController;

    public CustomerController() {
    }

    public Customer getSelected() {
        return selected;
    }

    public void setSelected(Customer selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CustomerFacade getFacade() {
        return ejbFacade;
    }
    /**
     * Sets the selected to the currently logged in user.
     */
    public void loadCurrentUser() {
        String email = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
        selected = customerFacade.findByEmail(email);
    }
    /**
     * 
     * @return Returns an empty Customer and sets him to USER-Role
     */
    public Customer prepareCreate() {
        selected = new Customer();
        selected.getRoles().add(Customer.UserRole.USER);
        initializeEmbeddableKey();
        return selected;
    }
    /**
     * Forwards to registrationpage and calls prepareCreate().
     * @return 
     */
    public String prepareRegistration() {  // hand over customer to registration
        prepareCreate();
        return "register";
    }

    public String create(){
        Customer user = customerFacade.findByEmail(selected.getEmail());
        if (user.getFirstname()!=null) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("ValidationMessages").getString("alreadyInUse"));
        } else {
            persist(PersistAction.CREATE, JsfUtil.getMessage(FacesContext.getCurrentInstance(), "CustomerCreated"));
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return "index";
    }

    public String update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessage(FacesContext.getCurrentInstance(), "CustomerUpdated"));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return "index";
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessage(FacesContext.getCurrentInstance(), "CustomerDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
/**
 * Removes a location from the list of locations and prepares a new location.
 * @param location The locations that will be deleted.
 */
    public void deleteLocation(Location location) {
        selected.getLocations().remove(location);
        update();
        locationController.setSelected(location);
        locationController.destroy();
        locationController.prepareCreate();
    }

    public List<Customer> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void initCurrentUser() throws IOException {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String email = ctx.getExternalContext().getRemoteUser();
        setSelected(customerFacade.findByEmail(email));
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

    public Customer getCustomer(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Customer> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Customer> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Customer.class)
    public static class CustomerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CustomerController controller = (CustomerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "customerController");
            return controller.getCustomer(getKey(value));
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
            if (object instanceof Customer) {
                Customer o = (Customer) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Customer.class.getName()});
                return null;
            }
        }

    }
    
    

}
