package com.epam.robot.helpers;

import com.epam.robot.common.ResponseCode;
import com.epam.robot.messages.responces.GenericResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by Dmytro_Kovalskyi on 18.07.2014.
 */
public class ResponseHelper {
    public static GenericResponse createResponse(int messageType) {
        GenericResponse response = null;
        response = new GenericResponse();
        response.setResponseCode(ResponseCode.STATUS_OK);
        response.setMessageType(messageType);
        return response;
    }

    public static GenericResponse createResponse(int messageType, Object data) {
        GenericResponse response = createResponse(messageType);
        response.setResponse(data);
        return response;
    }

    public static GenericResponse createFail(Object data) {
        GenericResponse response = null;
        response = new GenericResponse();
        response.setResponseCode(ResponseCode.STATUS_FAIL);
        return response;
    }

    public static String createFailJson(Object data) {
        String result = "Error";
        GenericResponse response = createFail(data);
        try {
            result = JsonHelper.toJson(response);
        } catch (JsonProcessingException e) {
        }
        return result;
    }
}
