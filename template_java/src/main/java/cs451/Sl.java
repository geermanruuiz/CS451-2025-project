package cs451;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

public class Sl extends Fll {

    //private Fll fll;
    private List<Pair<Integer, Integer>> sent;
    Timer timer;


    public Sl(int id, int port, InetAddress address, String outputPath, HashMap<Integer, Integer> hosts) {
        super(id, port, address, outputPath, hosts);
        // fll = new Fll(id, port, address, outputPath, hosts);
        sent = new ArrayList<>();

    }

    public void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resendAll();
            }
        }, 1000, 5000);
    }

    public int deliver(int sender, int msg) {
        return super.deliver(sender, msg);
    }

    public void send(int receiverPort, int msg) {
        super.send(receiverPort, msg);
        sent.add(new Pair<>(receiverPort, msg));
    }

    private void resendAll() {
        for (Pair<Integer, Integer> p : sent) {
            int receiverPort = p.getKey();
            int msg = p.getValue();
            super.send(receiverPort, msg);
        }
        System.out.println("Resent all messages.");
    }

    public int handleReceive(DatagramPacket packet){
        int sender = packet.getPort();
        int msg = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
        Pair<Integer, Integer> pair = new Pair<>(sender, msg);

        // add() is atomic — returns false if it already existed
        boolean firstTime = delivered.add(pair);
        return firstTime ? 0 : -1;
    }
}
