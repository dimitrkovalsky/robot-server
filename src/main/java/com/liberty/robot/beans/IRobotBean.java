package com.liberty.robot.beans;

import com.liberty.robot.messages.KeyPressedMessage;

import java.util.function.Consumer;
import javax.ejb.Local;

/**
 * User: dimitr
 * Date: 11.05.2015
 * Time: 10:43
 */
@Local
public interface IRobotBean extends Consumer<KeyPressedMessage> {
}
