package com.feiliks.rms.dto;

import com.feiliks.rms.entities.Request;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class RequestStatusDTO {

    @NotNull(message = "Request status must not be null.")
    private String status;

    @AssertTrue(message = "Request status is invalid.")
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
