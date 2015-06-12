package com.liberty.robot.messages.responces;

/**
 * Created by Dmytro_Kovalskyi on 18.07.2014.
 */
public class ConnectionEstablishedMessage {
    private int deviceId;

    public ConnectionEstablishedMessage(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}
