package sample;

import javafx.scene.control.TextArea;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static sample.Controller.CONTROLLER;

public class TCP extends Thread{

    static Socket clientSocket = null;
    static String address = null;
    static int port = 0;
    static String host = null;

    private int frequency = 1000;
    boolean stoped = false;

    public TCP(String address, int frequency){
        this.address = address;
        this.frequency = frequency;
    }

    static void setTextArea(TextArea t){

    }
    void stopClient(){
        stoped = true;
    }
    static void connection() throws IOException {
        String[] temp = address.split(":");
        host = temp[0];
        port = Integer.parseInt(temp[1]);
        new LastServer().addServer(address);
        clientSocket = new Socket(host, port);
    }

    void setAddress(String address){
        String[] temp = address.split(":");
        host = temp[0];
        port = Integer.parseInt(temp[1]);
    }
    String getAddress(){
        return address;
    }
    static void message(String msg) throws IOException {
        String displayBytes;

        long T1, T2, Tcli, Tserv, delta;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());

        DataInputStream inFromServer = new DataInputStream(clientSocket.getInputStream());
        inFromUser = new BufferedReader(new StringReader(msg));
        System.out.println("From Client: "+ msg);

        T1 = System.currentTimeMillis();
        outToServer.writeUTF(msg);
        outToServer.flush();

        displayBytes = inFromServer.readUTF();
        Tserv = Long.parseLong(displayBytes);
        T2 = System.currentTimeMillis();
        Tcli = T2;

        delta = Tserv + (T2-T1)/2 - Tcli;
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(Tcli+delta),
                ZoneId.systemDefault());
        System.out.println("Time: "+zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)+" delta: " + delta);
        CONTROLLER.appendText("Time: "+zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)+" delta: " + delta);
        //consoleTextArea.appendText("Time: "+zdt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)+" delta: " + delta);
    }

    @Override
    public void run() {
        try {
            connection();
        } catch (Exception e) {
            CONTROLLER.appendText("CONNECTION ERROR");
        }
            try {
                while(!stoped) {
                    message("TIME");
                    Thread.sleep(frequency);
                }
            } catch (InterruptedException | IOException e) {
                CONTROLLER.appendText("ERROR SENDING MESSAGES");
                e.printStackTrace();
            }

        try {
            if(clientSocket != null)
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
