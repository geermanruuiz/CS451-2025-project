package cs451;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Base UDP server inspired in the blog post: https://www.baeldung.com/udp-in-java

public class Fll extends Thread{
    protected DatagramSocket socket;
    protected int port;
    protected InetAddress address;
    protected int myId;
    protected boolean running;
    protected byte[] buf = new byte[256];
    protected OutputWriter outputWriter;
    protected HashMap<Integer, Integer> hosts;
    protected List<Pair<Integer, Integer>> delivered;

    public Fll(int id, int port, InetAddress address, String outputPath, HashMap<Integer, Integer> hosts) {
        try{
            socket = new DatagramSocket(port, address);
        } catch(SocketException e){
            e.printStackTrace();
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        }
            System.out.println("Runnin on port: " + socket.getLocalPort());
            this.port = port;
            this.address = address;
            this.myId = id;
            running = false;
            outputWriter = new OutputWriter(outputPath);
            this.hosts = hosts;
            delivered = new ArrayList<>();
    }

    public DatagramPacket receive(){
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try{
            // Se queda esperando a que le llegue un paquete
            System.out.println("Waiting for a packet...");
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int senderPort = packet.getPort();
        String received_string = new String(packet.getData(), 0, packet.getLength());
        int received = Integer.parseInt(received_string);

        return packet;
    }

    public void send(int receiverPort, int msg){
        buf = Integer.toString(msg).getBytes();
        DatagramPacket packet = new DatagramPacket (buf, buf.length, address, receiverPort);
        System.out.println("Sending package...");
        try{
            socket.send(packet);
            System.out.println("Package sent to port!" + receiverPort + "\n");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error sending package!");
            System.exit(1);
        }
    }

    public void deliver(int sender, int msg){
        delivered.add(new Pair<>(sender, msg));
    }

    public int getMyId(){
        return this.myId;
    }

    public void writeReceive(int senderPOrt, int received) {
        outputWriter.writeRecieve(hosts.get(senderPOrt), received);
        System.out.println("He recibido el paquete: " + received);
    }

    public void writeSend(int msg){
        outputWriter.writeSend(msg);
    }
}
