package com.epam.robot.beans;

import com.epam.robot.messages.KeyPressedMessage;

import java.util.function.Consumer;

/**
 * User: dimitr
 * Date: 11.05.2015
 * Time: 10:43
 */
public interface IRobotBean extends Consumer<KeyPressedMessage> {
}
