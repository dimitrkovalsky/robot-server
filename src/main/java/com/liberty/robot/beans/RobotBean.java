package com.liberty.robot.beans;

import com.liberty.robot.common.ConnectionProperties;
import com.liberty.robot.helpers.JsonHelper;
import com.liberty.robot.messages.requests.GenericRequest;
import com.liberty.robot.utils.EventBus;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static com.liberty.robot.utils.LoggingUtil.error;
import static com.liberty.robot.utils.LoggingUtil.info;

/**
 * User: dimitr
 * Date: 11.05.2015
 * Time: 10:42
 */
@Singleton
@Startup
public class RobotBean implements IRobotBean {

    private RobotServer server;

    @PostConstruct
    public void init() {
        System.out.println("Called from thread : " + Thread.currentThread().getName());
        server = new RobotServer();
        new Thread(server).start();
    }

    @PreDestroy
    private void destroy() {
        System.out.println("[RobotBean] destroy");
    }

    @Override
    public void accept(GenericRequest genericMessage) {
        System.out.println("INCOMING genericMessage : " + genericMessage);
        try {
            server.sendData(JsonHelper.toJson(genericMessage));
        }
        catch (Throwable e) {
            System.err.println("[RobotBean] error : " + e.getMessage());
        }
    }


    class RobotServer implements Runnable {
        private SocketProcessor processor = null;

        @Override
        public void run() {
            try {
                System.out.println(
                        "[RobotServer] started waiting for robot connection on port " + ConnectionProperties.MESSAGE_PORT);
                ServerSocket ss = new ServerSocket(ConnectionProperties.MESSAGE_PORT);
                while (true) {
                    Socket s = ss.accept();
                    System.out.println("Raspberry client accepted");
                    this.processor = new SocketProcessor(s);
                    new Thread(this.processor).start();
                }
            }
            catch (Throwable e) {
                System.err.println("[RobotServer] error : " + e.getMessage());
            }
        }

        public void sendData(String data) throws Throwable {
            if (this.processor != null) {
                this.processor.writeResponse(data);
            }
            else {
                System.err.println("[RobotBean] Robot doesn't connected");
            }
        }
        class SocketProcessor implements Runnable {

            private Socket socket;
            private DataInputStream input;
            private PrintWriter out = null;

            public SocketProcessor(Socket s) throws Throwable {
                this.socket = s;
//                this.input = new DataInputStream(s.getInputStream());
                input = new DataInputStream(s.getInputStream());

                this.out = new PrintWriter(s.getOutputStream());
            }

            private void read() {
                String inputData;
                try {
                    while ((inputData = input.readLine()) != null) {
                        info(this, "Received : " + inputData);
                        GenericRequest message = JsonHelper.toEntity(inputData, GenericRequest.class);
                        if (message != null)
                            EventBus.fireEvent(message);
                        else
                            error(this, "Incoming message is incorrect");
                    }
                    System.err.println("Connection closed");
                }
                catch (Throwable t) {
                    error(this, "Error : " + t.getMessage());
                }
            }

            public void run() {
                try {
                    new Thread(this::read).start();
                } catch(Throwable t) {
                    System.err.println("[SocketProcessor] an error occurred : " + t.getMessage());
                    if(socket != null) {
                        try {
                            socket.close();
                        } catch(IOException e) {
                            System.err.println("Error : " + e.getMessage());
                        }
                    }
                }
            }

            private void writeResponse(String data) throws Throwable {
                out.println(data);
                out.flush();
            }
        }
    }
}
