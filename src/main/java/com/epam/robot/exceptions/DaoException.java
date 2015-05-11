package com.epam.robot.exceptions;

/**
 * Created by Dmytro_Kovalskyi on 17.07.2014.
 */
public class DaoException extends ApplicationException
{
    public DaoException(String message) {
        super(message);
    }

    public DaoException() {
        super();
    }

    public DaoException(Exception cause) {
        super(cause);
    }

    public DaoException(String message, Exception cause) {
        super(cause);
    }
}