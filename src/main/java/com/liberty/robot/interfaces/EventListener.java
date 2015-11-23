package com.liberty.robot.interfaces;

import com.liberty.robot.messages.requests.GenericRequest;

/**
 * Created by Dmytro_Kovalskyi on 28.10.2015.
 */
public interface EventListener {
    void onMessage(GenericRequest message);
}
