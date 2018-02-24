package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.CheckPoint;
import com.feiliks.testapp2.jpa.entities.Request;
import com.feiliks.testapp2.jpa.entities.Requirement;
import com.feiliks.testapp2.jpa.entities.Tag;
import com.feiliks.testapp2.jpa.entities.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RequirementDTO {

    private Long id;

    @NotNull(message = "Requirement title must not be null.")
    @Size(min = 1, max = 127, message = "Requirement title is invalid (1-127 characters).")
    private String title;

    private int priority;
    private String content;
    private Date created = new Date();
    private Date modified;
    private UserDTO owner;
    private Set<CheckPointDTO> checkPoints;
    private Set<UserDTO> participants;
    private Set<String> tags;
    private Set<RequestDTO> requests; // belongs to

    public RequirementDTO() {
    }

    public RequirementDTO(Requirement requirement) {
        id = requirement.getId();
        title = requirement.getTitle();
        priority = requirement.getPriority();
        content = requirement.getContent();
        created = requirement.getCreated();
        modified = requirement.getModified();
        owner = new UserDTO(requirement.getOwner());

        Set<CheckPointDTO> cps = new HashSet<>();
        if (requirement.getCheckPoints() != null) {
            for (CheckPoint cp : requirement.getCheckPoints()) {
                cps.add(new CheckPointDTO(cp));
            }
        }
        checkPoints = cps;

        Set<UserDTO> us = new HashSet<>();
        if (requirement.getParticipants() != null) {
            for (User u : requirement.getParticipants()) {
                us.add(new UserDTO(u));
            }
        }
        participants = us;

        Set<RequestDTO> rqs = new HashSet<>();
        if (requirement.getRequests() != null) {
            for (Request r : requirement.getRequests()) {
                rqs.add(new RequestDTO(r));
            }
        }
        requests = rqs;

        Set<String> ts = new HashSet<>();
        if (requirement.getTags() != null) {
            for (Tag t : requirement.getTags()) {
                ts.add(t.getName());
            }
        }
        tags = ts;

    }

    public Requirement toEntity() {
        Requirement e = new Requirement();
        e.setId(id);
        e.setTitle(title);
        e.setPriority(priority);
        e.setContent(content);
        e.setCreated(created);
        e.setModified(modified);
        e.setOwner(owner == null ? null : owner.toEntity());
        Collection<CheckPoint> cps = new ArrayList<>();
        for (CheckPointDTO cp : getCheckPoints()) {
            CheckPoint cpEntity = cp.toEntity();
            cpEntity.setRequirement(e);
            cps.add(cpEntity);
        }
        e.setCheckPoints(cps);
        Collection<Tag> ts = new ArrayList<>();
        for (String t : getTags()) {
            if (t != null && !t.isEmpty()) {
                ts.add(new Tag(t));
            }
        }
        e.setTags(ts);
        Set<User> us = new HashSet<>();
        for (UserDTO u : getParticipants()) {
            us.add(u.toEntity());
        }
        e.setParticipants(us);
        Set<Request> rqs = new HashSet<>();
        for (RequestDTO rq : getRequests()) {
            rqs.add(rq.toEntity());
        }
        e.setRequests(rqs);
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
     * @return the checkPoints
     */
    public Set<CheckPointDTO> getCheckPoints() {
        return checkPoints == null ? new HashSet<CheckPointDTO>() : checkPoints;
    }

    /**
     * @param checkPoints the checkPoints to set
     */
    public void setCheckPoints(Set<CheckPointDTO> checkPoints) {
        this.checkPoints = checkPoints;
    }

    /**
     * @return the participants
     */
    public Set<UserDTO> getParticipants() {
        return participants == null ? new HashSet<UserDTO>() : participants;
    }

    /**
     * @param participants the participants to set
     */
    public void setParticipants(Set<UserDTO> participants) {
        this.participants = participants;
    }

    /**
     * @return the tags
     */
    public Set<String> getTags() {
        return tags == null ? new HashSet<String>() : tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * @return the requests
     */
    public Set<RequestDTO> getRequests() {
        return requests == null ? new HashSet<RequestDTO>() : requests;
    }

    /**
     * @param requests the requests to set
     */
    public void setRequests(Set<RequestDTO> requests) {
        this.requests = requests;
    }

}
