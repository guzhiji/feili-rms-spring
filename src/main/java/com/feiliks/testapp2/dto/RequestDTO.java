package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.Request;
import java.util.Date;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequestDTO {

    private Long id;

    @NotNull(message = "Request title must not be null.")
    @Size(min = 1, max = 127, message = "Request title is invalid (1-127 characters).")
    private String title;

    private String content;
    private Date created;
    private Date modified;
    private UserDTO owner;

    @NotNull(message = "Request type must not be null.")
    private RequestTypeDTO requestType;

    @NotNull(message = "Request status must not be null.")
    private Request.Status status;

    public RequestDTO() {
        status = Request.Status.OPEN;
        Date n = new Date();
        created = n;
        modified = n;
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

    public Request toEntity() {
        Request e = new Request();
        e.setId(id);
        e.setTitle(title);
        e.setContent(content);
        e.setCreated(created);
        e.setModified(modified);
        e.setOwner(owner == null ? null : owner.toEntity());
        e.setRequestType(requestType == null ? null : requestType.toEntity());
        e.setStatus(status);
        return e;
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
