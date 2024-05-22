package com.Douane.backendPFE.DTOs.request;

public class ContactDto {
    private String subject;
    private String message;

    // Getters et Setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
