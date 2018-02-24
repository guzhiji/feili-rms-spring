package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.RequestType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestTypeDTO {

    private Long id;

    @NotNull(message = "Request type name must not be null.")
    @Size(min = 1, max = 63, message = "Request type name is invalid (1-63 characters).")
    private String name;

    @NotNull(message = "Manager must not be null.")
    private UserDTO manager;

    public RequestTypeDTO() {
    }

    public RequestTypeDTO(RequestType requestType) {
        id = requestType.getId();
        name = requestType.getName();
        manager = new UserDTO(requestType.getManager());
    }

    public RequestType toEntity() {
        RequestType e = new RequestType();
        e.setId(id);
        e.setName(name);
        e.setManager(manager == null ? null : manager.toEntity());
        return e;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserDTO getManager() {
        return manager;
    }

    public void setManager(UserDTO user) {
        manager = user;
    }

}
