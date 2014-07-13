package at.fhjoanneum.swenga.project.audiocracyweb.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Kevin
 */
@Named
@SessionScoped
public class LanguageSelectorBean implements Serializable {

    private Locale locale;

    public LanguageSelectorBean() {
        locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

    public void changeLocale(String loc) {
        locale = new Locale(loc);
    }

    public String getLocale() {
        return locale.toString();
    }

    public List<String> getSupportedLocales() {
        List<String> li = new ArrayList();
        Iterator<Locale> i = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        while (i.hasNext()) {
            Locale l = i.next();
            li.add(l.toString());
        }
        return li;
    }

}
