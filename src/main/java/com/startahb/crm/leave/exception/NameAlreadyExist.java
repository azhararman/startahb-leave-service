package com.startahb.crm.leave.exception;

public class NameAlreadyExist extends RuntimeException {

    public NameAlreadyExist(String message)
    {
        super(message);
    }
    public NameAlreadyExist(String message,Throwable cause)
    {
        super(message,cause);
    }

}

