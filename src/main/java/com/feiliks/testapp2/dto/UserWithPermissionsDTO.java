package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.User;
import java.util.Set;

public class UserWithPermissionsDTO extends UserDTO {

    private Set<User.Permission> permissions;

    public UserWithPermissionsDTO() {
    }

    public UserWithPermissionsDTO(User user) {
        super(user);
        permissions = user.getPermissionsAsSet();
    }

    @Override
    public User toEntity() {
        User entity = super.toEntity();
        entity.setPermissions(permissions);
        return entity;
    }

    /**
     * @return the permissions
     */
    public Set<User.Permission> getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(Set<User.Permission> permissions) {
        this.permissions = permissions;
    }

}
