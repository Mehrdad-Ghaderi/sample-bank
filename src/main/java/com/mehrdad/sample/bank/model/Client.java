package com.mehrdad.sample.bank.model;

import java.io.Serializable;

public class Client implements Serializable {

    private boolean member;
    private final String name;
    private final String id;
    private String phoneNumber;

    public Client(String id, String name, String phoneNumber, boolean isMember) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.member = isMember;
    }

    public boolean isNotMember() {
        return !member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
                "member=" + member +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

}