package com.bank.service;

import com.bank.Bank;

import java.io.*;

public class BackupService {

    private static final String PATH = "C:\\Users\\Metallica\\IdeaProjects\\sample-bank\\src\\com\\bank\\BankData.ser";

    public static void backup(Object object, String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bank restoreBackup() {
        try {
            FileInputStream fileInputStream = new FileInputStream(PATH);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Bank bank = (Bank) objectInputStream.readObject();

            if (bank != null) {
                return bank;
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
            System.out.println("There is no information to restore.");
            return new Bank();
    }

    public static String getPATH() {
        return PATH;
    }

}
