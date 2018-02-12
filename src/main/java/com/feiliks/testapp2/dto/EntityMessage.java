package com.feiliks.testapp2.dto;

public class EntityMessage<T> {

    private String status;
    private T entity;

    public EntityMessage() {
    }

    public EntityMessage(String status, T entity) {
        this.status = status;
        this.entity = entity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public T getEntity() {
        return entity;
    }
}
