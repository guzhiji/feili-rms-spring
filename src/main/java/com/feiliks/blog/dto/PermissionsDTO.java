package com.feiliks.blog.dto;

import com.feiliks.blog.entities.UserPermission;

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

    public Set<UserPermission> getPermissionsAsSet() {
        Set<UserPermission> pset = new HashSet<>();
        for (String p : permissions) {
            pset.add(UserPermission.valueOf(p));
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
