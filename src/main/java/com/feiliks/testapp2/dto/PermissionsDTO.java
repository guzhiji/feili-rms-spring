package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.User;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;

public class PermissionsDTO {

    @NotNull(message = "Permissions must not be null.")
    private Set<String> permissions;

    @AssertTrue(message = "Permissions contain invalid values.")
    private boolean isValid() {
        if (permissions == null) {
            return true;
        }
        try {
            getPermissionsAsSet();
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public Set<User.Permission> getPermissionsAsSet() {
        Set<User.Permission> pset = new HashSet<>();
        for (String p : permissions) {
            pset.add(User.Permission.valueOf(p));
        }
        return pset;
    }

    /**
     * @return the permissions
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}
