package com.feiliks.testapp2.dto;

public class Message {

    private String status;
    private String content;

    public Message() {
    }

    public Message(String type, String content) {
        this.status = type;
        this.content = content;
    }

    public void setStatus(String type) {
        this.status = type;
    }

    public String getStatus() {
        return status;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
