package com.feiliks.rms.dto;

import com.feiliks.rms.entities.CheckPoint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class CheckPointStatusDTO {

    @NotNull(message = "Check point status must not be null.")
    private String status;

    @NotNull(message = "Check point days left must not be null.")
    private Integer daysLeft;

    @AssertTrue(message = "Check point status is invalid.")
    @JsonIgnore
    public boolean isStatusValid() {
        try {
            getStatusAsObject();
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @JsonIgnore
    public CheckPoint.Status getStatusAsObject() {
        return CheckPoint.Status.valueOf(status);
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the daysLeft
     */
    public Integer getDaysLeft() {
        return daysLeft;
    }

    /**
     * @param daysLeft the daysLeft to set
     */
    public void setDaysLeft(Integer daysLeft) {
        this.daysLeft = daysLeft;
    }

}
