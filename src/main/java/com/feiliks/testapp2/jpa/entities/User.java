package com.feiliks.testapp2.jpa.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

    public enum Permission {
        ALL, MANAGE_REQUEST_TYPES, MANAGE_USERS
    }

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

    @Pattern(regexp = "^[ \\-+0-9]{3,16}$", message = "Phone number is invalid.")
    private String phone;

    @Pattern(regexp = "^.+@.+$", message = "Email is invalid.")
    @Size(max = 128, message = "E-mail address must not be longer than 128.")
    private String email;

    private String permissions;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private Set<Requirement> requirementsParticipated;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Requirement> requirementsOwned;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Request> requestsOwned;

    @OneToMany(mappedBy = "manager")
    @JsonIgnore
    private Set<RequestType> requestTypesManaged;

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

    /**
     * @return the requestTypesManaged
     */
    public Set<RequestType> getRequestTypesManaged() {
        return requestTypesManaged;
    }

    /**
     * @param requestTypesManaged the requestTypesManaged to set
     */
    public void setRequestTypesManaged(Set<RequestType> requestTypesManaged) {
        this.requestTypesManaged = requestTypesManaged;
    }

    /**
     * @return the permissions
     */
    public String getPermissions() {
        return permissions;
    }

    @JsonIgnore
    public Set<Permission> getPermissionsAsSet() {
        Set<Permission> out = new HashSet<>();
        if (permissions != null) {
            for (String p : permissions.split(",")) {
                p = p.trim();
                if (!p.isEmpty()) {
                    try {
                        out.add(Permission.valueOf(p));
                    } catch (IllegalArgumentException ex) {
                    }
                }
            }
        }
        return out;
    }

    @JsonIgnore
    public boolean hasPermissions(Permission... perms) {
        Set<Permission> owned = getPermissionsAsSet();
        if (owned.contains(Permission.ALL)) {
            return true;
        }
        List<Permission> required = new ArrayList<>(Arrays.asList(perms));
        required.remove(Permission.ALL);
        return owned.containsAll(required);
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        StringBuilder sb = new StringBuilder();
        for (Permission p : permissions) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.name());
        }
        setPermissions(sb.toString());
    }

    public void setPermissions(Collection<Permission> permissions) {
        setPermissions(new HashSet<>(permissions));
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        if (hash == 0) {
            hash += (username != null ? username.hashCode() : 0);
        }
        if (hash != 0) {
            return hash;
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if (this.id == null) {
            if (this.username == null) {
                return super.equals(object);
            }
            return this.username.equals(other.username);
        }
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + ", username=" + username + " ]";
    }

}
