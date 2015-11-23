package com.liberty.robot.endpoints;

import com.liberty.robot.beans.IRobotBean;
import com.liberty.robot.common.MessageTypes;
import com.liberty.robot.helpers.JsonHelper;
import com.liberty.robot.helpers.MessageProcessor;
import com.liberty.robot.interfaces.EventListener;
import com.liberty.robot.messages.requests.GenericRequest;
import com.liberty.robot.utils.EventBus;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

import static com.liberty.robot.utils.LoggingUtil.error;
import static com.liberty.robot.utils.LoggingUtil.info;

@ServerEndpoint("/control")
public class ControlEndpoint extends ResponseEndpoint {
    private EventListener eventListener = new RobotListener();
    @Inject
    private IRobotBean robotBean;

    @OnOpen
    public void onOpen(Session session) {
        info("Connection opened : " + session.getId());
        this.session = session;
        EventBus.subscribe(session.getId(), eventListener);
    }

    @OnClose
    public void onClose(Session session) {
        info(this, "Connection closed : " + session.getId());
        EventBus.unSubscribe(session.getId());
        this.session = null;
    }

    @OnError
    public void onError(Throwable error) {
        EventBus.unSubscribe(session.getId());
        session = null;
        deviceId = null;
        error(this, "Error with : " + error.getMessage());
    }

    @OnMessage
    public void onWebMessage(String message) {
        try {
            System.out.println("[ControlEndpoint] on message : " + message);
            GenericRequest request = MessageProcessor.parse(message);
            System.out.println("Bean : " + robotBean);
            switch (request.getMessageType()) {
                case MessageTypes.CONNECTION_ESTABLISHED:
                    System.out.println("[ControlEndpoint] Web client connected : ");
                    break;
                default:
                    robotBean.accept(request);
                    //System.out.println("[ControlEndpoint] unrecognized message type : " + request);
            }

        }
        catch (Exception e) {
            System.err.println("[ControlEndpoint] error : " + e.getMessage());
            sendResponse(e);
        }
    }

    private class RobotListener implements EventListener {
        @Override
        public void onMessage(GenericRequest message) {
            info(this, "onMessage : " + message);
            if (session != null) {
                try {
                    session.getBasicRemote().sendText(JsonHelper.toJson(message));
                }
                catch (IOException e) {
                    error(this, e);
                }
            }
            else {
                error(this, " session is null");
            }
        }
    }
}
