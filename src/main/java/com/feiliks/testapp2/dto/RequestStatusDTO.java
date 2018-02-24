package com.feiliks.testapp2.dto;

import com.feiliks.testapp2.jpa.entities.Request;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;

public class RequestStatusDTO {

    @NotNull(message = "Request status must not be null.")
    private String status;

    @AssertTrue(message = "Request status is invalid.")
    public boolean isStatusValid() {
        try {
            getStatusAsObject();
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public Request.Status getStatusAsObject() {
        return Request.Status.valueOf(status);
    }

    public void setStatus(String s) {
        status = s;
    }

    public String getStatus() {
        return status;
    }

}