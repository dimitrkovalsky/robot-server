package com.epam.robot.beans;

import com.epam.robot.exceptions.DaoException;
import com.epam.robot.messages.KeyPressedMessage;

import javax.annotation.PreDestroy;
import javax.ejb.Startup;
import javax.enterprise.inject.spi.Producer;
import javax.inject.Singleton;
import java.util.HashMap;

/**
 * User: dimitr
 * Date: 11.05.2015
 * Time: 10:42
 */
@Singleton
@Startup
public class RobotBean implements IRobotBean {

    @PreDestroy
    private void destroy() {
        System.out.println("[RobotBean] destroy");
    }

    @Override
    public void accept(KeyPressedMessage keyPressedMessage) {
        System.out.println("INCOMING KeyPressedMessage : " + keyPressedMessage);
    }
}
