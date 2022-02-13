package com.mehrdad.sample.bank.api.dto;

import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Component
public class ClientDto {

    private String id;
    private String name;
    private String phoneNumber;
    private Boolean active;

    public ClientDto(String id, String name, String phoneNumber, boolean status) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.active = status;
    }

    public ClientDto() {

    }

    @Id
    @Size(max = 10)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NotNull
    @Size(max = 45)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotNull
    @Size(max = 15)
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NotNull
    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean status) {
        this.active = status;
    }

    @Override
    public String toString() {
        return String.format("Client{id='%s', name='%s', phoneNumber='%s', active=%s}",
                id, name, phoneNumber, active);
    }

}