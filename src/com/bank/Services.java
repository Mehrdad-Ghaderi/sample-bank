package com.bank;

import java.io.*;

class Services implements Serializable{

    private static final String PATH = "C:\\Users\\Metallica\\IdeaProjects\\sample-bank\\src\\com\\bank\\BankData.ser";



    static void backup(Object object, String path) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Bank restoreBackup() {
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

    static String getPATH() {
        return PATH;
    }

}
