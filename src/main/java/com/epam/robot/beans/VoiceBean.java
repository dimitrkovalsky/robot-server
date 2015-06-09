package com.epam.robot.beans;

import com.epam.robot.common.ConnectionProperties;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 * Created by Dmytro_Kovalskyi on 09.06.2015.
 */
@Singleton
@Startup
public class VoiceBean implements IVoiceBean{

    @PostConstruct
    public void init() {
        System.out.println("[VoiceBean] initialization");
        VoiceServer server = new VoiceServer();
        new Thread(server).start();
    }

    class VoiceServer implements Runnable {
        private SocketProcessor processor = null;
        private SourceDataLine sourceDataLine;

        public VoiceServer() {
            init();
        }

        private void init() {
            try {
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, getAudioFormat());
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceDataLine.open(getAudioFormat());
                sourceDataLine.start();
                System.out.println("[VoiceServer] initialized");
            } catch(Exception e) {
                System.err.println("[VoiceServer] init error : " + e.getMessage());
            }

        }

        private void destroy() {
            sourceDataLine.close();
        }


        private void toSpeaker(byte soundbytes[]) {
//        System.out.println("Bytes received " + Arrays.toString(soundbytes));
            try {
                sourceDataLine.write(soundbytes, 0, soundbytes.length);
//            sourceDataLine.drain();
            } catch(Exception e) {
                System.out.println("Not working in speakers " + e.getMessage());
            }

        }

        public AudioFormat getAudioFormat() {
            float sampleRate = 8000.0F;
            //8000,11025,16000,22050,44100
            int sampleSizeInBits = 16;
            //8,16
            int channels = 1;
            //1,2
            boolean signed = true;
            //true,false
            boolean bigEndian = false;
            //true,false
            return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        }

        @Override
        public void run() {
            try {
                System.out.println("[VoiceServer] started waiting for robot connection on port "
                    + ConnectionProperties.VOICE_PORT);
                ServerSocket ss = new ServerSocket(ConnectionProperties.VOICE_PORT);
                while(true) {
                    Socket s = ss.accept();
                    System.out.println("[VoiceServer] Raspberry client accepted");
                    processor = new SocketProcessor(s, this::toSpeaker);
                    new Thread(processor).start();
                }
            } catch(Throwable e) {
                processor = null;
                System.err.println("[VoiceServer] error : " + e.getMessage());
            }
        }


        class SocketProcessor implements Runnable {

            private Socket socket;
            private BufferedInputStream input;
            private byte[] buffer = new byte[1024];
            private PrintWriter out = null;
            private Consumer<byte[]> onRead;

            private SocketProcessor(Socket s, Consumer<byte[]> onRead) throws Throwable {
                this.socket = s;
                input = new BufferedInputStream(s.getInputStream());
                this.out = new PrintWriter(s.getOutputStream());
                this.onRead = onRead;
            }

            private void read() {
                try {
                    System.out.println("[SocketProcessor] Wait for messages");
                    byte[] buffer = new byte[1024];
                    boolean end = false;
                    String dataString = "";
                    while(!end) {
                        int bytesRead = input.read(buffer);
                        onRead.accept(buffer);

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

        }
    }
}
