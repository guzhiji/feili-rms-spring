package com.feiliks.rms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.feiliks.common.entities.User;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Lob;
import javax.persistence.TemporalType;

@Entity
@Table(name = "rms_request")
public class Request implements Serializable {

    public static enum Status {
        OPEN, CLOSED
    };

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 127)
//    @NotNull(message = "Request title must not be null.")
//    @Size(min = 1, max = 127, message = "Request title is invalid (1-127 characters).")
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @ManyToOne
    private User owner;

    @ManyToMany(mappedBy = "requests")
    @JsonIgnore
    private Set<Requirement> requirements;

    @ManyToOne(optional = false)
    @JoinColumn(name = "request_type_id")
    private RequestType requestType;

    @Column(nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private Status status;

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
    public User getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * @return the requirements
     */
    public Set<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * @param requirements the requirements to set
     */
    public void setRequirements(Set<Requirement> requirements) {
        this.requirements = requirements;
    }

    /**
     * @return the requestType
     */
    public RequestType getType() {
        return requestType;
    }

    /**
     * @param requestType the requestType to set
     */
    public void setType(RequestType requestType) {
        this.requestType = requestType;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        if (hash != 0) {
            return hash;
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Request)) {
            return false;
        }
        if (this.id == null) {
            return super.equals(object);
        }
        Request other = (Request) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + " ]";
    }

}
