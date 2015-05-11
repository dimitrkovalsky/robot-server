package com.epam.robot.endpoints;

import com.epam.robot.beans.IRobotBean;
import com.epam.robot.common.MessageTypes;
import com.epam.robot.helpers.JsonHelper;
import com.epam.robot.helpers.MessageProcessor;
import com.epam.robot.messages.KeyPressedMessage;
import com.epam.robot.messages.requests.GenericRequest;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/control")
public class ControlEndpoint extends ResponseEnpoint {
    @Inject
    private IRobotBean robotBean;

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("[ControlEndpoint] Connection opened : " + session.getId());
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("[ControlEndpoint] Connection closed : " + session.getId());
        this.session = null;
    }

    @OnError
    public void onError(Throwable error) {
        this.session = null;
        deviceId = null;
        System.err.println("[ControlEndpoint] Error with : " + error.getMessage());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            System.out.println("[ControlEndpoint] on message : " + message);
            GenericRequest request = MessageProcessor.parse(message);
            switch (request.getMessageType()) {
                case MessageTypes.CONNECTION_ESTABLISHED:
                    System.out.println("[ControlEndpoint] Web client connected : ");
                    break;
                case MessageTypes.KEY_PRESSED:
                    KeyPressedMessage msg = JsonHelper.toKeyPressedMessage(request.getRequestData());
                    robotBean.accept(msg);
                    break;
                default:
                    System.out.println("[ControlEndpoint] unrecognized message type : " + request);
            }
        } catch (Exception e) {
            System.err.println("[ControlEndpoint] error : " + e.getMessage());
            sendResponse(e);
        }
    }
}
