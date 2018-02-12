package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.RequestType;

public class RequestTypeDTO {

    private Long id;
    private String name;
    private UserDTO manager;

    public RequestTypeDTO() {
    }

    public RequestTypeDTO(RequestType requestType) {
        id = requestType.getId();
        name = requestType.getName();
        manager = new UserDTO(requestType.getManager());
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
