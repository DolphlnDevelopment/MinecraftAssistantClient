package com.dolphln.assistantclient.core;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Client extends Thread {

    private Socket socket;
    private UUID identifier;
    private String linkingCode;
    private Label textLabel;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public Client(Label textLabel, String ip, int port) {
        identifier = UUID.randomUUID();
        this.textLabel = textLabel;
        this.connect(ip, port);
    }

    private void connect(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            socket.setKeepAlive(true);

            createStreams();

            this.dataOutputStream.writeByte(1);
            this.dataOutputStream.writeUTF(this.identifier.toString());
            this.dataOutputStream.flush();

            boolean done = false;
            while (!done) {
                byte messageType = this.dataInputStream.readByte();

                switch (messageType) {
                    case 1 -> {
                        textLabel.setText("\nConnection successful!\n");
                        System.out.println("\nConnection successful!\n");
                        linkingCode = "/link " + this.dataInputStream.readUTF();
                        textLabel.setText("Use the command " + this.linkingCode + " to start using the voice assistant.");
                        System.out.println("Use the command " + this.linkingCode + " to start using the voice assistant.");
                        done = true;
                    }
                    case -1 -> {
                        closeStreams();
                        this.socket.close();
                        done = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(5);
        }
    }

    public void createStreams() {
        try {
            if (this.dataInputStream == null) {
                this.dataInputStream = new DataInputStream(this.socket.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if (this.dataOutputStream == null) {
                this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeStreams() {
        try {
            this.dataInputStream.close();
            this.dataInputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            this.dataOutputStream.close();
            this.dataOutputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendText(String text) {
        try {
            this.dataOutputStream.writeByte(2);
            this.dataOutputStream.writeUTF(text);
            this.dataOutputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void sendCommand(String command) {
        this.sendText(command);
    }

}
