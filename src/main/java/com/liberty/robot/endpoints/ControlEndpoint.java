package com.liberty.robot.endpoints;

import com.liberty.robot.beans.IRobotBean;
import com.liberty.robot.common.MessageTypes;
import com.liberty.robot.helpers.JsonHelper;
import com.liberty.robot.helpers.MessageProcessor;
import com.liberty.robot.messages.KeyPressedMessage;
import com.liberty.robot.messages.requests.GenericRequest;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/control")
public class ControlEndpoint extends ResponseEndpoint {
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
            System.out.println("Bean : " + robotBean);
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
