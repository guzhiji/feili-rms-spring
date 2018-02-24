package com.feiliks.testapp2.jpa.entities;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Entity
@Table(name = "rms_checkpoint")
public class CheckPoint implements Serializable {

    public static enum Status {
        PENDING, WORKING, DONE
    };

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
//    @NotNull(message = "Checkpoint description must not be null.")
//    @Size(min = 1, max = 255, message = "Checkpoint description is invalid (1-255 characters).")
    private String description;

    @Column(nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
//    @NotNull(message = "Checkpoint status must not be null.")
    private Status status;

    @Column(nullable = false)
    private int daysLeft;

    @ManyToOne(optional = false)
//    @NotNull(message = "Checkpoint parent requirement must not be null.")
    private Requirement requirement;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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

    /**
     * @return the daysLeft
     */
    public int getDaysLeft() {
        return daysLeft;
    }

    /**
     * @param daysLeft the daysLeft to set
     */
    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    /**
     * @return the requirement
     */
    public Requirement getRequirement() {
        return requirement;
    }

    /**
     * @param requirement the requirement to set
     */
    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
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
        if (!(object instanceof CheckPoint)) {
            return false;
        }
        if (this.id == null) {
            return super.equals(object);
        }
        CheckPoint other = (CheckPoint) object;
        return this.id.equals(other.id);
    }

    @Override
    public String toString() {
        return getClass().getCanonicalName() + "[ id=" + id + " ]";
    }

}
