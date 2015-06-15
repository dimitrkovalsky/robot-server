package com.liberty.robot.beans;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Created by Dmytro_Kovalskyi on 11.06.2015.
 */
@Singleton
@Startup
public class ConfigurationBean {
    private static final String PROPERTIES_NAME = "robot.properties";
    private static int DEFAULT_VOICE_BUFFER_SIZE = 1024;

    private Properties properties = new Properties();
    private boolean voiceLogEnabled = true;
    private boolean voiceRecordingEnabled = true;
    private boolean speakersEnabled = true;
    private int voiceBuffer;


    @PostConstruct
    public void readConfig() throws Exception {
        String fileName = System.getProperty("jboss.server.config.dir") + "\\"+ PROPERTIES_NAME;
        File file = new File(fileName);
        properties.load(new FileInputStream(file));
        processProperties();
        System.out.println("Properties read : " + this.toString());
    }

    private void processProperties() {
        voiceLogEnabled = Boolean.parseBoolean(properties.getProperty("voiceLog", "false"));
        voiceRecordingEnabled = Boolean.parseBoolean(properties.getProperty("microphone", "false"));
        speakersEnabled = Boolean.parseBoolean(properties.getProperty("speakers", "false"));
        voiceBuffer = Integer.parseInt(
            properties.getProperty("voiceBuffer", String.valueOf(DEFAULT_VOICE_BUFFER_SIZE)));
    }

    public boolean isVoiceLogEnabled() {
        return voiceLogEnabled;
    }

    public boolean isVoiceRecordingEnabled() {
        return voiceRecordingEnabled;
    }

    public boolean isSpeakersEnabled() {
        return speakersEnabled;
    }

    public int getVoiceBufferSize() {
        return voiceBuffer;
    }

    @Override
    public String toString() {
        return "Config{" +
            "voiceBuffer=" + voiceBuffer +
            ", speakersEnabled=" + speakersEnabled +
            ", voiceRecordingEnabled=" + voiceRecordingEnabled +
            ", voiceLogEnabled=" + voiceLogEnabled +
            '}';
    }
}
