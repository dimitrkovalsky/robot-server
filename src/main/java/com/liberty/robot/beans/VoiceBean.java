package com.liberty.robot.beans;

import com.liberty.robot.common.ConnectionProperties;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Created by Dmytro_Kovalskyi on 09.06.2015.
 */
@Singleton
@Startup
public class VoiceBean implements IVoiceBean {
    @Inject
    private ConfigurationBean config;

    @PostConstruct
    public void init() {
        System.out.println("[VoiceBean] initialization");
        VoiceServer server = new VoiceServer();
        new Thread(server).start();
    }

    class VoiceServer implements Runnable {
        private SocketProcessor processor = null;
        private SourceDataLine speakersDataLine;
        private TargetDataLine microphoneDataLine;

        public VoiceServer() {

        }

        private void init() {
            if(config.isSpeakersEnabled()) {
                initSpeakers();
            }
            if(config.isVoiceRecordingEnabled()) {
                initMicrophone();
                new Thread(this::startRecording).start();
            }
        }

        private void initMicrophone() {
            System.out.println("[VoiceBean] voice recording started");
            if(AudioSystem.isLineSupported(Port.Info.MICROPHONE)) {
                try {
                    DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, getAudioFormat());
                    microphoneDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                    microphoneDataLine.open(getAudioFormat());

                } catch(Exception e) {
                    System.out.println("[VoiceBean] not correct : " + e.getMessage());
                    System.out.println("MICROPHONE doesn't supported");
                }
            } else {
                System.err.println("[VoiceBean] microphone not supported");
            }
        }

        private void initSpeakers() {
            try {
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, getAudioFormat());
                speakersDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                speakersDataLine.open(getAudioFormat());
                speakersDataLine.start();
                System.out.println("[VoiceServer] initialized");
            } catch(Exception e) {
                System.err.println("[VoiceServer] init error : " + e.getMessage());
            }
        }

        private void startRecording() {
            try {
                while(processor == null) {
                    Thread.sleep(10);
                }
                microphoneDataLine.start();
                byte tempBuffer[] = new byte[config.getVoiceBufferSize()];
                while(true) {
                    microphoneDataLine.read(tempBuffer, 0, tempBuffer.length);
                    processor.sent(tempBuffer);
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
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
                    init();
                    processor = new SocketProcessor(s, this::toSpeaker);
                    new Thread(processor).start();
                }
            } catch(Throwable e) {
                processor = null;
                System.err.println("[VoiceServer] error : " + e.getMessage());
            }
        }

        private void destroy() {
            speakersDataLine.close();
        }


        private void toSpeaker(byte soundbytes[]) {
            if(config.isVoiceLogEnabled()) {
                System.out.println("Voice received " + Arrays.toString(soundbytes));
            }
            try {
                speakersDataLine.write(soundbytes, 0, soundbytes.length);
//            speakersDataLine.drain();
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


        class SocketProcessor implements Runnable {

            private Socket socket;
            private BufferedInputStream input;
            private byte[] buffer = new byte[1024];
            private OutputStream out;
            private Consumer<byte[]> onRead;

            private SocketProcessor(Socket s, Consumer<byte[]> onRead) throws Throwable {
                this.socket = s;
                input = new BufferedInputStream(s.getInputStream());
                this.out = s.getOutputStream();
                this.onRead = onRead;
            }

            private void read() {
                try {
                    System.out.println("[SocketProcessor] Wait for messages");
                    byte[] buffer = new byte[config.getVoiceBufferSize()];
                    boolean end = false;
                    while(true) {
                        input.read(buffer);
                        onRead.accept(buffer);
                    }
                } catch(Throwable t) {
                    System.err.println("Error t : " + t.getMessage());
                }
            }

            public void sent(byte[] content) {
                try {
                    if(config.isVoiceLogEnabled()) {
                        System.out.println("Recorded voice : " + Arrays.toString(content));
                    }
                    out.write(content);
                    out.flush();
                } catch(Throwable t) {
                    System.err.println("Error : " + t.getMessage());
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
