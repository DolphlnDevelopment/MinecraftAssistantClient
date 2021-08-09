package com.dolphln.assistantclient;

import com.dolphln.assistantclient.core.Client;
import com.dolphln.assistantclient.core.VoiceManager;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Main {

    private static VoiceManager voiceManager;
    private static Client client;

    private static Label textLabel;

    public static void main(String[] st) {
        //1. Create the frame.
        JFrame frame = new JFrame("FrameDemo");

        //2. Optional: What happens when the frame closes?
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);

        //3. Create components and put them in the frame.
        textLabel = new Label("Loading window...");
        textLabel.setSize(400, 50);
        frame.getContentPane().add(textLabel, BorderLayout.CENTER);

        Button button = new Button("Close");
        button.setSize(200, 25);
        button.addActionListener(e -> System.exit(0));
        frame.getContentPane().add(button, BorderLayout.CENTER);

        //5. Show it.
        frame.setVisible(true);

        System.out.println("Loading config...\n");
        textLabel.setText("Loading config...\n");
        JsonObject jsonConfig = JsonParser.parseString("{\"ip\": \"127.0.0.1\", \"port\": 8000}").getAsJsonObject();

        try {
            File configFile = new File("config.json");
            if (!configFile.exists()) {
                configFile.createNewFile();

                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("config.json"));
                bufferedWriter.write(jsonConfig.toString());
                bufferedWriter.close();
            }
            jsonConfig = JsonParser.parseReader(new FileReader("config.json")).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(4);
        }

        textLabel.setText("Starting voice recognition...\n");
        System.out.println("Starting voice recognition...\n");
        voiceManager = new VoiceManager(false);

        String ip = jsonConfig.get("ip").getAsString();
        int port = jsonConfig.get("port").getAsInt();
        textLabel.setText("Connecting to Server " + ip + ":" + port + "...");
        System.out.println("Connecting to Server " + ip + ":" + port + "...");
        client = new Client(textLabel, ip, port);
        client.start();

        voiceManager.startVoice();
    }

    public static ArrayList<String> load(String path) {
        ArrayList<String> lines = new ArrayList<>();

        InputStream inputStream = Main.class.getResourceAsStream(path);
        try (InputStreamReader streamReader =
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static VoiceManager getVoiceManager() {
        return voiceManager;
    }

    public static Client getClient() {
        return client;
    }
}
