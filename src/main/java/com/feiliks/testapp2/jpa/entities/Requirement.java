package com.feiliks.testapp2.jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.TemporalType;

@Entity
@Table(name = "rms_requirement")
public class Requirement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 127)
//    @NotNull(message = "Requirement title must not be null.")
//    @Size(min = 1, max = 127, message = "Requirement title is invalid (1-127 characters).")
    private String title;

    @Column(nullable = false)
    private int priority;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @ManyToOne
    private User owner;

    @ManyToMany
    private Set<Request> requests;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "requirement", orphanRemoval = true)
    private Collection<CheckPoint> checkPoints;

    @ManyToMany(cascade = {CascadeType.ALL})
    private Collection<User> participants;

    @ManyToMany(cascade = {CascadeType.ALL})
    private Collection<Tag> tags;

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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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
     * @return the requests
     */
    public Set<Request> getRequests() {
        return requests;
    }

    /**
     * @param requests the requests to set
     */
    public void setRequests(Set<Request> requests) {
        this.requests = requests;
    }

    /**
     * @return the checkPoints
     */
    public Collection<CheckPoint> getCheckPoints() {
        return checkPoints;
    }

    /**
     * @param checkPoints the checkPoints to set
     */
    public void setCheckPoints(Collection<CheckPoint> checkPoints) {
        this.checkPoints = checkPoints;
    }

    /**
     * @return the participants
     */
    public Collection<User> getParticipants() {
        return participants;
    }

    /**
     * @param participants the participants to set
     */
    public void setParticipants(Collection<User> participants) {
        this.participants = participants;
    }

    /**
     * @return the tags
     */
    public Collection<Tag> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Requirement)) {
            return false;
        }
        Requirement other = (Requirement) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + " ]";
    }

}
