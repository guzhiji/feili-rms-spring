package com.feiliks.testapp2.jpa.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

@Entity
@Table(name = "rms_requirement")
@NamedQuery(
        name = "Requirement.findParticipated",
        query = "select r from Requirement r JOIN r.participants u where u.id = ?1"
)
@NamedNativeQuery(
        name = "Requirement.findParticipated2",
        query = "select r.* from rms_requirement r inner join rms_requirement_participant u on u.requirement_id = r.id where u.participant_id = ?1 limit ?2,?3",
        resultClass = Requirement.class
)
public class Requirement implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 127)
    private String title;

    @Column(nullable = false)
    private int priority;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @ManyToOne
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "rms_requirement_request",
            joinColumns = @JoinColumn(name = "requirement_id"),
            inverseJoinColumns = @JoinColumn(name = "request_id")
    )
    private Set<Request> requests;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "requirement", orphanRemoval = true)
    private Collection<CheckPoint> checkPoints;

    @ManyToMany
    @JoinTable(
            name = "rms_requirement_participant",
            joinColumns = @JoinColumn(name = "requirement_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Collection<User> participants;

    @ManyToMany
    @JoinTable(
            name = "rms_requirement_tag",
            joinColumns = @JoinColumn(name = "requirement_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
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
        if (hash != 0) {
            return hash;
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Requirement)) {
            return false;
        }
        if (this.id == null) {
            return super.equals(object);
        }
        Requirement other = (Requirement) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + " ]";
    }

}
