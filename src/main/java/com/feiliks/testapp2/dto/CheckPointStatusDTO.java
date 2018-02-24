package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.CheckPoint;
import javax.validation.constraints.NotNull;

public class CheckPointStatusDTO {

    @NotNull(message = "Check point status must not be null.")
    private CheckPoint.Status status;

    @NotNull(message = "Check point days left must not be null.")
    private Integer daysLeft;

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
