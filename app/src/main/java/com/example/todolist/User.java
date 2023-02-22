//Student Name: Megan Cash
//Student Number: C19317723
package com.example.todolist;

public class User {

    //Variables - User is defined as having an email, password, name and phone number.
    public String email, password, fullName, phoneNumber;

    public User() {

    }

    public User(String email, String password, String fullName, String phoneNumber) {

        this.email=email;
        this.password=password;
        this.fullName=fullName;
        this.phoneNumber=phoneNumber;
    }
}