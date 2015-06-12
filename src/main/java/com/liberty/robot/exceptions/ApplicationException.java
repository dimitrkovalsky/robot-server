package com.liberty.robot.exceptions;

/**
 * Created by Dmytro_Kovalskyi on 17.07.2014.
 */
public class ApplicationException extends Exception{
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException() {
        super();
    }

    public ApplicationException(Exception cause) {
        super(cause);
    }

    public ApplicationException(String message, Exception cause) {
        super(cause);
    }
}
