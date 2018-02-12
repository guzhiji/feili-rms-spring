package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.CheckPoint;

public class CheckPointDTO {

    private Long id;
    private String description;
    private CheckPoint.Status status;
    private int daysLeft;

    public CheckPointDTO() {
    }

    public CheckPointDTO(CheckPoint checkPoint) {
        id = checkPoint.getId();
        description = checkPoint.getDescription();
        status = checkPoint.getStatus();
        daysLeft = checkPoint.getDaysLeft();
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
