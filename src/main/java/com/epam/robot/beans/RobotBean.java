package com.epam.robot.beans;

import com.epam.robot.helpers.JsonHelper;
import com.epam.robot.messages.KeyPressedMessage;
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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * User: dimitr
 * Date: 11.05.2015
 * Time: 10:42
 */
@Singleton
@Startup
public class RobotBean implements IRobotBean {
    public static final int PORT = 5555;

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
                System.out.println("[RobotServer] started waiting for robot connection on port " + PORT);
                ServerSocket ss = new ServerSocket(PORT);
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

        private void play() throws LineUnavailableException, IOException {
            byte[] buffer = new byte[1024];
            InputStream input = new ByteArrayInputStream(buffer);
            final AudioFormat format = new AudioFormat(1,1,1,true,true);;
            final AudioInputStream ais = new AudioInputStream(input, format, buffer.length /format.getFrameSize());
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine sline = (SourceDataLine) AudioSystem.getLine(info);
            sline = sline;
            sline.open(format);
            sline.start();
            Float audioLen = (buffer.length / format.getFrameSize()) * format.getFrameRate();

            int bufferSize = (int) format.getSampleRate() * format.getFrameSize();
            byte buffer2[] = new byte[bufferSize];
            int count2;


            ais.read( buffer2, 0, buffer2.length);
            sline.write(buffer2, 0, buffer2.length);
            sline.flush();
            sline.drain();
            sline.stop();
            sline.close();
            buffer2 = null;
        }
        class SocketProcessor implements Runnable {

            private Socket socket;
            private InputStream input;
            private byte[] buffer = new byte[1024];
            private PrintWriter out = null;

            private SocketProcessor(Socket s) throws Throwable {
                this.socket = s;
                input = new ByteArrayInputStream(buffer)s.getInputStream());
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
                    while(!end)
                    {
                        int bytesRead = in.read(messageByte);
                        String messageString = new String(messageByte, 0, bytesRead);
//                        if (messageString.length() == 100)
//                        {
//                            end = true;
//                        }
                        System.out.println("MESSAGE: " + messageString);
                    }
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
