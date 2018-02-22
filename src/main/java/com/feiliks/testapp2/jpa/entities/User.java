package com.feiliks.testapp2.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "rms_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull(message = "Username must not be null.")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]{3,64}$", message = "Username is invalid.")
    private String username;

    @Column(nullable = false)
    @NotNull(message = "Password must not be null.")
    @Size(min = 6, max = 128, message = "Length of password should be between 6 to 128.")
    private String password;

    private String phone;

    @Pattern(regexp = "^.+@.+$", message = "Email is invalid.")
    private String email;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<Requirement> requirementsParticipated;
    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Requirement> requirementsOwned;
    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Request> requestsOwned;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String n) {
        username = n;
    }

    // @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        password = pass;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the requirementsParticipated
     */
    public Set<Requirement> getRequirementsParticipated() {
        return requirementsParticipated;
    }

    /**
     * @param requirementsParticipated the requirementsParticipated to set
     */
    public void setRequirementsParticipated(Set<Requirement> requirementsParticipated) {
        this.requirementsParticipated = requirementsParticipated;
    }

    /**
     * @return the requirementsOwned
     */
    public Set<Requirement> getRequirementsOwned() {
        return requirementsOwned;
    }

    /**
     * @param requirementsOwned the requirementsOwned to set
     */
    public void setRequirementsOwned(Set<Requirement> requirementsOwned) {
        this.requirementsOwned = requirementsOwned;
    }

    /**
     * @return the requestsOwned
     */
    public Set<Request> getRequestsOwned() {
        return requestsOwned;
    }

    /**
     * @param requestsOwned the requestsOwned to set
     */
    public void setRequestsOwned(Set<Request> requestsOwned) {
        this.requestsOwned = requestsOwned;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (username != null ? username.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.username == null && other.username != null) || (this.username != null && !this.username.equals(other.username))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + ", username=" + username + " ]";
    }

}