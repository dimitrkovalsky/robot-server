package com.epam.robot.helpers;

import com.epam.robot.exceptions.ApplicationException;
import com.epam.robot.messages.requests.GenericRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Dmytro_Kovalskyi on 18.07.2014.
 */
public class MessageProcessor {
    public static GenericRequest parse(String message) throws ApplicationException {
        try {
            ObjectMapper mapper = JsonHelper.getMapper();
            GenericRequest genericRequest = mapper.readValue(message, GenericRequest.class);
            return genericRequest;
        } catch (Exception e) {
            throw new ApplicationException(e);
        }
    }
}
