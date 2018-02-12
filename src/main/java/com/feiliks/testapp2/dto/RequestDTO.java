package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.Request;
import java.util.Date;

public class RequestDTO {

    private Long id;
    private String title;
    private String content;
    private Date created;
    private Date modified;
    private UserDTO owner;
    private RequestTypeDTO requestType;
    private Request.Status status;

    public RequestDTO() {
    }

    public RequestDTO(Request request) {
        id = request.getId();
        title = request.getTitle();
        content = request.getContent();
        created = request.getCreated();
        modified = request.getModified();
        owner = new UserDTO(request.getOwner());
        requestType = new RequestTypeDTO(request.getRequestType());
        status = request.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String n) {
        title = n;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String c) {
        content = c;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date c) {
        created = c;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date m) {
        modified = m;
    }

    /**
     * @return the owner
     */
    public UserDTO getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(UserDTO owner) {
        this.owner = owner;
    }

    /**
     * @return the requestType
     */
    public RequestTypeDTO getRequestType() {
        return requestType;
    }

    /**
     * @param requestType the requestType to set
     */
    public void setRequestType(RequestTypeDTO requestType) {
        this.requestType = requestType;
    }

    /**
     * @return the status
     */
    public Request.Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Request.Status status) {
        this.status = status;
    }

}
