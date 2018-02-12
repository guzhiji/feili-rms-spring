package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.CheckPoint;
import com.feiliks.testapp2.jpa.entities.Requirement;
import com.feiliks.testapp2.jpa.entities.Tag;
import com.feiliks.testapp2.jpa.entities.User;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RequirementDTO {

    private Long id;
    private String title;
    private int priority;
    private String content;
    private Date created;
    private Date modified;
    private UserDTO owner;
    private Set<CheckPointDTO> checkPoints;
    private Set<UserDTO> participants;
    private Set<Tag> tags;

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
        for (CheckPoint cp : requirement.getCheckPoints()) {
            cps.add(new CheckPointDTO(cp));
        }
        checkPoints = cps;

        Set<UserDTO> us = new HashSet<>();
        for (User u : requirement.getParticipants()) {
            us.add(new UserDTO(u));
        }
        participants = us;

        tags = requirement.getTags();

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
        return checkPoints;
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
        return participants;
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
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

}
