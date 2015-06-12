package com.liberty.robot.beans;

import com.liberty.robot.common.ConnectionProperties;
import com.liberty.robot.helpers.JsonHelper;
import com.liberty.robot.messages.KeyPressedMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;

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
        server = new RobotServer();
        new Thread(server).start();
    }

    @PreDestroy
    private void destroy() {
        System.out.println("[RobotBean] destroy");
    }

    @Override
    public void accept(KeyPressedMessage keyPressedMessage) {
        System.out.println("INCOMING KeyPressedMessage : " + keyPressedMessage);
        try {
            server.sendData(JsonHelper.toJson(keyPressedMessage));
        } catch(Throwable e) {
            System.err.println("[RobotBean] error : " + e.getMessage());
        }
    }


    class RobotServer implements Runnable {
        private SocketProcessor processor = null;

        @Override
        public void run() {
            try {
                System.out.println("[RobotServer] started waiting for robot connection on port " +
                    ConnectionProperties.MESSAGE_PORT);
                ServerSocket ss = new ServerSocket(ConnectionProperties.MESSAGE_PORT);
                while(true) {
                    Socket s = ss.accept();
                    System.out.println("Raspberry client accepted");
                    processor = new SocketProcessor(s);
                    new Thread(processor).start();
                }
            } catch(Throwable e) {
                processor = null;
                System.err.println("[RobotServer] error : " + e.getMessage());
            }
        }

        public void sendData(String data) throws Throwable {
            if(processor != null) {
                processor.writeResponse(data);
            } else {
                System.err.println("[RobotBean] Robot doesn't connected");
            }
        }
        class SocketProcessor implements Runnable {

            private Socket socket;
            private InputStream input;
            private byte[] buffer = new byte[1024];
            private PrintWriter out = null;

            private SocketProcessor(Socket s) throws Throwable {
                this.socket = s;
                input = new ByteArrayInputStream(buffer);
                this.out = new PrintWriter(s.getOutputStream());
            }

            private void read() {
                try {
                    String input;
                    System.out.println("[SocketProcessor] Wait for messages");
                    byte[] buffer = new byte[1024];
                    byte[] messageByte = new byte[1000];
                    boolean end = false;
                    String dataString = "";
                } catch(Throwable t) {
                    System.err.println("Error t : " + t.getMessage());
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
