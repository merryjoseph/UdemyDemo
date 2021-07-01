package com.example.PasswordEmailSecurityDemo.Exceptions;

public class UserServiceExceptions extends RuntimeException{

    private static final long serialVersionUID = 1348771109171435607L;
    public UserServiceExceptions(String message){
        super(message);
    }
}
