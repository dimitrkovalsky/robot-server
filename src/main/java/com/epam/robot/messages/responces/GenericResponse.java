package com.epam.robot.messages.responces;

/**
 * Created by Dmytro_Kovalskyi on 18.07.2014.
 */
public class GenericResponse {
    protected int messageType;
    protected int responseCode;
    protected Object response;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
