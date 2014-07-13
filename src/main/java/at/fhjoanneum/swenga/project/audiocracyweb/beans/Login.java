/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.fhjoanneum.swenga.project.audiocracyweb.beans;

import at.fhjoanneum.swenga.project.audiocracyweb.controller.util.JsfUtil;
import at.fhjoanneum.swenga.project.audiocracyweb.entities.Customer;
import at.fhjoanneum.swenga.project.audiocracyweb.facades.CustomerFacade;
import java.io.IOException;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author paumgarten
 */
@ManagedBean
public class Login implements Serializable {

    Customer authenticatedUser;
    FacesContext context = FacesContext.getCurrentInstance();
    @Inject
    CustomerFacade customerFacade;

    public Customer getAuthenticatedUser() {
        return authenticatedUser;
    }

    public void setAuthenticatedUser(Customer authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public Login() {
    }

    public String signOut(HttpServletRequest request) throws ServletException {
        request.logout();

        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("locales/Bundle").getString("signOut"));
        //        Keep messages
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return "index";
    }
    /**
     * Handles the login of a user with the given email address and password.
     * @param request
     * @param email
     * @param password
     * @throws IOException
     * @throws ServletException 
     */
    public void login(HttpServletRequest request, String email, String password) throws IOException, ServletException {
        try {
            Customer user = customerFacade.findByEmail(email);
            request.login(user.getEmail(), password);
            if (user.isEnabled()) {
                authenticatedUser = user;
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("locales/Bundle").getString("signedIn"));
            }
        } catch (ServletException e) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("locales/Bundle").getString("InvalidLogin"));
        }
    }

}
