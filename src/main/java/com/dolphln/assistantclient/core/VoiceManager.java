package com.dolphln.assistantclient.core;

import com.dolphln.assistantclient.Main;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class VoiceManager {

    private boolean enabled;

    private LiveSpeechRecognizer liveSpeechRecognizer;
    private ArrayList<String> commands;

    public VoiceManager() {
        this(false);
    }

    public VoiceManager(boolean enable) {
        try {
            this.initialize();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Couldn't start voice speech recognition. Exiting the program...");
            System.exit(-1);
        }

        this.commands = Main.load("/commands.txt");

        enabled = false;
        if (enable) {
            this.startVoice();
        }
    }

    private void initialize() throws IOException {
        Configuration config = new Configuration();

        config.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        //config.setDictionaryPath("src\\main\\resources\\8670.dic");
        //config.setLanguageModelPath("src\\main\\resources\\8670.lm");
        config.setDictionaryPath("resource:/8670.dic");
        config.setLanguageModelPath("resource:/8670.lm");

        // Disabling debug messages
        /*Logger cmRootLogger = Logger.getLogger("default.config");
        cmRootLogger.setLevel(java.util.logging.Level.OFF);
        String conFile = System.getProperty("java.util.logging.config.file");
        if (conFile == null) {
            System.setProperty("java.util.logging.config.file", "ignoreAllSphinx4LoggingOutput");
        }*/

        this.liveSpeechRecognizer = new LiveSpeechRecognizer(config);
    }

    public void startVoice() {
        if (enabled) return;
        this.enabled = true;
        this.liveSpeechRecognizer.startRecognition(true);

        SpeechResult speechResult = null;
        while (this.enabled) {
            speechResult = this.liveSpeechRecognizer.getResult();

            ArrayList<String> commands = new ArrayList<>();

            for (String preCommand : this.commands) {
                if (speechResult.getHypothesis().toLowerCase().contains(preCommand)) {
                    commands.add(preCommand);
                }
            }

            if (commands.size() == 0) continue;

            for (String command : commands) {
                System.out.println("Voice command detected " + command);

                Main.getClient().sendCommand(command);
            }
        }
    }

    public void stopVoice() {
        if (!enabled) return;
        this.enabled = false;
        this.liveSpeechRecognizer.stopRecognition();
    }
}
