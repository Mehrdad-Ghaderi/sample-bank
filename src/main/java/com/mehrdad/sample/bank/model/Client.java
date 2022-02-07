package com.mehrdad.sample.bank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity // shows there a table of this element in DB
public class Client {

    private String id;
    private String name;
    private String phoneNumber;
    private Boolean active;

    protected Client() {
    }

    public Client(String id, String name, String phoneNumber, Boolean isMember) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.active = isMember;
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

    public void setActive(Boolean member) {
        this.active = member;
    }

    @Override
    public String toString() {
        return String.format("Client{id='%s', name='%s', phoneNumber='%s', member=%s}",
                id, name, phoneNumber, active);
    }

}