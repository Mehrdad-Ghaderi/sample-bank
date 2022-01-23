package com.bank;

import java.io.Serializable;

public class Client implements Serializable {
    private boolean isMember;
    private String name;
    private String id;
    private String phoneNumber;

    Client(String name, String phoneNumber, String id) {
        this.isMember = true;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
    }

    public boolean isNotMember() {
        return !isMember;
    }

    public void setMember(boolean member) {
        isMember = member;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
                "isMember=" + isMember +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

}