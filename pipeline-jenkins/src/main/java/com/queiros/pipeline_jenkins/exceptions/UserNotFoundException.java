package com.queiros.pipeline_jenkins.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(){
        super("User not found. ");
    }

}
