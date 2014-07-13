/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.fhjoanneum.swenga.project.audiocracyweb.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 *
 * @author Kevin
 */
@Entity
@NamedQueries({
 @NamedQuery(name = "Customer.findByEmail",
         query = "Select c from Customer c where c.email = :email")
})
public class Customer implements Serializable {

    public enum UserRole {USER, HOST, PREMIUMHOST, ADMIN};
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pattern(regexp = "\\D+", message = "{Name_with_Numbers}")
    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String firstname;

    @Pattern( regexp = "\\D+", message = "{Name_with_Numbers}")
    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String lastname;

    @NotBlank
    @Size(max = 80)
    @Email
    @Column(nullable = false, length = 80, unique = true)
    private String email;

    @NotBlank
    @Size(min = 8, message = "{greaterThan8}")    
    @Column(nullable = false)
    private String password;

    @ElementCollection @Enumerated(EnumType.STRING) @Size(min = 1)
    private List<UserRole> roles = new ArrayList<>();

    @Size(max = 8)
    @Column(length = 8)
    private String zipCode;

    @Size(max = 58)
    @Column(length = 58)
    private String city;

    @Size(max = 100)
    @Column(length = 100)
    private String street;

    @Size(max = 100)
    @Column(length = 100)
    private String country;
    
    @Size(max = 20)
    @Column(length = 20)
    private String tel;
    
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "customerId")
    private List<Location> locations = new ArrayList<Location>();
    
    

    public Customer() {
    }

    public Customer(String firstname, String lastname, String email, String zipCode, String city, String street, String country, String tel) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.zipCode = zipCode;
        this.city = city;
        this.street = street;
        this.country = country;
        this.tel = tel;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }
    
    

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }


    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return firstname + " " + lastname + " (" + email + ")";
    }
    
        public boolean isEnabled() {
        if (this == null){
            return false;
        }
        return true;
    }

}
