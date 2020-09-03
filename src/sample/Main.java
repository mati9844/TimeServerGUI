package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;

import static java.lang.Thread.sleep;
import static sample.Controller.CONTROLLER;

public class Main extends Application {

    @FXML
    private TextArea consoleTextArea;

    TextArea getTextArea(){
        return consoleTextArea;
    }
    static Client client = null;
    static TCP tcp = null;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("TimeServer Client");
        primaryStage.setScene(new Scene(root, 600, 475));
        primaryStage.show();
        Platform.runLater(this::addToChoiceBox);
        Platform.runLater(this::logToTexArea);


    }

    private void addToChoiceBox(){
        //CONTROLLER.ChoiceBox.getItems().add("TEST");
    }

    private void logToTexArea() {
        LastServer last = new LastServer();
        String x = last.getServer()+'\n';
        CONTROLLER.consoleTextArea.setText("ADD LOGGER OUTPUT HERE"+'\n'+x);

    }

    public static void main(String[] args) throws InterruptedException, IOException {
        client = new Client();
        client.init();
        launch(args);

    }
}

class MulticastSender implements Runnable{
    MulticastSocket multicastSocket = null;
    @Override
    public void run() {
        try {
            String message = "DISCOVERY";
            int mcPort = 7;
            String mcIPStr = "224.0.0.1";
            InetAddress mcIPAddress = null;

            mcIPAddress = InetAddress.getByName(mcIPStr);
            //DatagramSocket datagramSocket = new DatagramSocket();
            multicastSocket = new MulticastSocket(7);
            DatagramPacket packet_send = new DatagramPacket(message.getBytes(), message.getBytes().length);
            packet_send.setAddress(mcIPAddress);
            packet_send.setPort(mcPort);
            //datagramSocket.send(packet_send);
            multicastSocket.send(packet_send);
            System.out.println("Sent multicast message DISCOVERY");
            //multicastSocket.close();
            //datagramSocket.close();
            sleep(3000);
        }catch(Exception e){

        }

        if(multicastSocket != null)
            multicastSocket.close();
    }
}
class MulticastReceiver extends Thread{

    static int mcPort = 4446;
    static String mcIPStr = "224.0.0.1";
    static MulticastSocket mcSocket = null;
    static DatagramPacket packet = null;
    static InetAddress mcIPAddress = null;
    static String serverAddress = null;
    static int serverPort = -1;
    static boolean paused;
    static boolean stoped = false;

    static LinkedList<String> offers = new LinkedList<>();

    public void pauseReceiving(){
        paused = true;
    }
    public void startReceiving(){
        paused = false;
    }


    private void init() throws SocketException, UnknownHostException {
        this.paused = false;
        mcIPAddress = InetAddress.getByName(mcIPStr);
        try{
            mcSocket = new MulticastSocket(mcPort);
            mcSocket.joinGroup(mcIPAddress);
        }catch(IOException e){

        }
        mcSocket.setSoTimeout(5000);
    }
    public void stopReceiver() throws IOException {
        stoped = true;

    }

    LinkedList<String> getOffers(){
        return offers;
    }

    boolean getPaused(){
        return paused;
    }

    void setPaused(boolean paused){
        this.paused = paused;
    }

    public void print_offers(){
        for(String e : offers){
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            while(!stoped){
                Thread.sleep(1);
                try{
                    if(!paused) {
                        packet = new DatagramPacket(new byte[1024], 1024);
                        System.out.println("Waiting for a  multicast message...");
                        mcSocket.receive(packet);
                        String msg = new String(packet.getData(), packet.getOffset(),
                                packet.getLength());
                        if(msg.contains("OFFER")){
                            offers.add(msg);
                        }
                        System.out.println("Added");
                    }

                }catch(SocketTimeoutException e1) {
                    setPaused(true);
                    System.out.println("Pause receiving");
                }
            }
            mcSocket.leaveGroup(mcIPAddress);
            mcSocket.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
