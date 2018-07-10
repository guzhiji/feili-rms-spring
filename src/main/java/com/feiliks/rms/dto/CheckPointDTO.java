package com.feiliks.rms.dto;

import com.feiliks.rms.entities.CheckPoint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CheckPointDTO {

    private Long id;

    @NotNull(message = "Checkpoint description must not be null.")
    @Size(min = 1, max = 255, message = "Checkpoint description is invalid (1-255 characters).")
    private String description;

    @NotNull(message = "Checkpoint status must not be null.")
    private CheckPoint.Status status;

    private int daysLeft;

    public CheckPointDTO() {
        status = CheckPoint.Status.PENDING;
        daysLeft = 0;
    }

    public CheckPointDTO(CheckPoint checkPoint) {
        id = checkPoint.getId();
        description = checkPoint.getDescription();
        status = checkPoint.getStatus();
        daysLeft = checkPoint.getDaysLeft();
    }

    public CheckPoint toEntity() {
        CheckPoint e = new CheckPoint();
        e.setId(id);
        e.setDescription(description);
        e.setStatus(status);
        e.setDaysLeft(daysLeft);
        return e;
    }

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
    public CheckPoint.Status getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(CheckPoint.Status status) {
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

}
