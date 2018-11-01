package com.feiliks.blog.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;


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

    @Pattern(regexp = "^[ \\-+0-9]{3,16}$", message = "Phone number is invalid.")
    private String phone;

    @Pattern(regexp = "^.+@.+$", message = "Email is invalid.")
    @Size(max = 128, message = "E-mail address must not be longer than 128.")
    private String email;

    @JsonIgnore
    private String permissions;

    @OneToMany(mappedBy = "owner")
    @JsonIgnore
    private Set<Blog> blogs;

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
     * @return the blogs
     */
    public Set<Blog> getBlogs() {
        return blogs;
    }

    /**
     * @param blogs the blogs to set
     */
    public void setBlogs(Set<Blog> blogs) {
        this.blogs = blogs;
    }

    /**
     * @return the permissions
     */
    public String getPermissions() {
        return permissions;
    }

    @JsonIgnore
    public Set<UserPermission> getPermissionsAsSet() {
        Set<UserPermission> out = new HashSet<>();
        if (permissions != null) {
            for (String p : permissions.split(",")) {
                p = p.trim();
                if (!p.isEmpty()) {
                    try {
                        out.add(UserPermission.valueOf(p));
                    } catch (IllegalArgumentException ex) {
                    }
                }
            }
        }
        return out;
    }

    @JsonIgnore
    public boolean hasPermissions(UserPermission... perms) {
        Set<UserPermission> owned = getPermissionsAsSet();
        if (owned.contains(UserPermission.ALL)) {
            return true;
        }
        List<UserPermission> required = new ArrayList<>(Arrays.asList(perms));
        required.remove(UserPermission.ALL);
        return owned.containsAll(required);
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @JsonIgnore
    public void setPermissions(Set<UserPermission> permissions) {
        StringBuilder sb = new StringBuilder();
        for (UserPermission p : permissions) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(p.name());
        }
        setPermissions(sb.toString());
    }

    @JsonIgnore
    public void setPermissions(Collection<UserPermission> permissions) {
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
