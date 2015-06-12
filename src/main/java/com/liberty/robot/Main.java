package com.liberty.robot;

import com.liberty.robot.beans.RobotBean;
import com.liberty.robot.beans.VoiceBean;

/**
 * Created by Dmytro_Kovalskyi on 28.05.2015.
 */
public class Main {
    public static void main(String[] args) {
        RobotBean bean = new RobotBean();
        bean.init();
        VoiceBean voiceBean = new VoiceBean();
        voiceBean.init();
    }
}
