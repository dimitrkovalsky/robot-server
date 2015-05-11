package com.epam.robot.messages.requests;

/**
 * Created by Dmytro_Kovalskyi on 18.07.2014.
 */
public class GenericRequest {
    private int messageType;
    private Object requestData;

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Object getRequestData() {
        return requestData;
    }

    public void setRequestData(Object requestData) {
        this.requestData = requestData;
    }

    @Override
    public String toString() {
        return "GenericRequest{" +
                "requestData=" + requestData +
                ", messageType=" + messageType +
                '}';
    }
}
