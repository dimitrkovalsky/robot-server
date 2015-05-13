package com.epam.robot.endpoints;

import com.epam.robot.helpers.JsonHelper;
import com.epam.robot.helpers.ResponseHelper;
import com.epam.robot.messages.responces.GenericResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.websocket.Session;

/**
 * Created by Dmytro_Kovalskyi on 18.07.2014.
 */
public abstract class ResponseEndpoint {
    protected Session session;
    protected Integer deviceId;

    protected void sendResponse(GenericResponse response) {
        try {
            sendResponse(JsonHelper.toJson(response));
        } catch (JsonProcessingException e) {
            System.err.println("ERROR : " + e.getMessage());
            sendResponse(ResponseHelper.createFailJson("Data processing failed"));
        }
    }

    protected void sendResponse(String response) {
        try {
            if (session != null)
                session.getBasicRemote().sendText(response);
            else
                System.err.println("ERROR session is null");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendResponse(Exception exception) {
        sendResponse(ResponseHelper.createFailJson("Data processing failed : " + exception.getMessage()));
    }
}
