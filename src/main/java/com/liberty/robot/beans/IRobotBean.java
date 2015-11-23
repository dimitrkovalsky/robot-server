package com.liberty.robot.beans;

import com.liberty.robot.messages.requests.GenericRequest;

import javax.ejb.Local;
import java.util.function.Consumer;

/**
 * User: dimitr
 * Date: 11.05.2015
 * Time: 10:43
 */
@Local
public interface IRobotBean extends Consumer<GenericRequest> {
}
