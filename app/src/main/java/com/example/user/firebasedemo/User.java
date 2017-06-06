package com.example.user.firebasedemo;

/**
 * Created by ASUS on 6/6/2017.
 */

class User {

    private String name;
    private String email;
    private String password;
    String studentId;

    public User(){

    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }

    public String getStudentId(){
        return studentId;
    }

}
