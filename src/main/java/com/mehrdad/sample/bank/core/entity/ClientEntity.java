package com.mehrdad.sample.bank.core.entity;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity // shows there a table of this element in DB
public class ClientEntity {

    private String id;
    private String name;
    private String phoneNumber;
    private Boolean active;

    protected ClientEntity() {
    }

    public ClientEntity(String id, String name, String phoneNumber, Boolean status) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.active = status;
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
    @Column(name = "active", columnDefinition = "BIT default 1", length = 1)
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