package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;

import static java.lang.Thread.sleep;

public class TimeServer {
    static LinkedList<ServerSocket> serversSocket = new LinkedList<>();
    static LinkedList<InetAddress> inetAddresses = new LinkedList<>();
    static boolean isStopped = false;
    private static Thread TCPListenerThreadf;

    public void getInterfaces() {
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                //System.out.println("Net interface: " + ni.getName() + " - " + ni.getDisplayName());

                Enumeration<InetAddress> e2 = ni.getInetAddresses();
                if (e2.hasMoreElements()) {
                    InetAddress ip = e2.nextElement();
                    if (ip instanceof Inet4Address) {
                        //if(true){
                        //System.out.println("IP address: " + ip.toString());
                        ServerSocket serverSocket = new ServerSocket();
                        serverSocket.bind(new InetSocketAddress(ip, 0));
                        serversSocket.add(serverSocket);
                        inetAddresses.add(ip);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Start(){
        for (ServerSocket e : serversSocket) {
            TCPListenerThreadf = new Thread(new TCPListenerThread(e, this));
            TCPListenerThreadf.start();
        }

        for(ServerSocket e : serversSocket){
            Thread discoveryThread = new Thread(new DiscoveryThread(e));
            discoveryThread.start();
        }

    }
    public void Stop(){
        for (ServerSocket e : serversSocket) {
            try {
                System.out.println("Closed connection " + e.getInetAddress() + ":" + e.getLocalPort());
                e.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        TimeServer timeServer = new TimeServer();
        System.out.println("List all network interfaces example");
        //System.out.println();
        timeServer.getInterfaces();
        timeServer.Start();


    }

}

class TCPListenerThread implements Runnable {

    ServerSocket serverSocket;
    TimeServer timeServer;
    public TCPListenerThread(ServerSocket serverSocket, TimeServer timeServer) {
        this.serverSocket = serverSocket;
        this.timeServer = timeServer;
    }

    @Override
    public void run() {
        while(true){
            System.out.println("Server is listening on " + serverSocket.getInetAddress() +":"+serverSocket.getLocalPort());
            try {
                Socket serverClient = serverSocket.accept();
                TCPClientThread tcpClientThread = new TCPClientThread(serverClient);
                tcpClientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}

class TCPClientThread extends Thread{
    Socket serverClient;
    TCPClientThread(Socket clientThread){
        this.serverClient = clientThread;
    }
    public void run(){
        DataInputStream  inStream = null;
        try {
            inStream = new DataInputStream(serverClient.getInputStream());
            DataOutputStream outStream = new DataOutputStream(serverClient.getOutputStream());

            try {
                while(!serverClient.isClosed()) {
                    String clientMessage = inStream.readUTF();
                    if (clientMessage.toUpperCase().equals("TIME")) {
                        outStream.writeUTF(System.currentTimeMillis() + "");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            serverClient.close();
            inStream.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Client disconnect");
    }
}

/*
Inspiracja:
https://michieldemey.be/blog/network-discovery-using-udp-broadcast/
 */
class DiscoveryThread implements Runnable {
    MulticastSocket mcSocket = null;
    ServerSocket serverSocket;
    DatagramSocket udpSocket = null;
    InetAddress mcIPAddress_dis = null;
    int mcPort = 7;
    int mcPort2 = 4446;

    String mcIPStr = "224.0.0.1";

    public DiscoveryThread(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        Random r = new Random();

        try {
            mcSocket = new MulticastSocket(mcPort);
            mcIPAddress_dis = InetAddress.getByName(mcIPStr);
            mcSocket.joinGroup(mcIPAddress_dis);
            while(true) {
                DatagramPacket packet_dis = new DatagramPacket(new byte[1024], 1024);
                mcSocket.receive(packet_dis);
                String msg_dis = new String(packet_dis.getData(),
                        packet_dis.getOffset(),
                        packet_dis.getLength());

                if(msg_dis.toUpperCase().equals("DISCOVERY")) {
                    udpSocket = new DatagramSocket();
                    System.out.println(msg_dis);
                    InetAddress mcIPAddress = mcIPAddress_dis;
                    String address = serverSocket.getInetAddress().toString().substring(1);
                    byte[] msg = ("OFFER " + address + " " + serverSocket.getLocalPort()).getBytes();
                    DatagramPacket packet = new DatagramPacket(msg, msg.length);
                    packet.setAddress(mcIPAddress);
                    packet.setPort(4446);
                    udpSocket.send(packet);
                    sleep(r.nextInt(1000));

                    System.out.println(new String(new String(packet.getData(),
                            packet.getOffset(),
                            packet.getLength())));
                    udpSocket.close();
                }
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

}