package com.liberty.robot.utils;


import com.liberty.robot.interfaces.EventListener;
import com.liberty.robot.messages.requests.GenericRequest;

import java.util.HashMap;
import java.util.Map;

import static com.liberty.robot.utils.LoggingUtil.info;

/**
 * Created by Dmytro_Kovalskyi on 28.10.2015.
 */
public class EventBus {
    private static Map<String, EventListener> listeners = new HashMap<>();

    public static void subscribe(String name, EventListener listener) {
        listeners.put(name, listener);
    }

    public static void unSubscribe(String name) {
        listeners.remove(name);
    }

    /**
     * Fires event.
     */
    public static void fireEvent(GenericRequest data) {
        info(EventBus.class, "event retrieved send to " + listeners.size() + " listeners");
        listeners.values().forEach(l -> l.onMessage(data));
    }
}
