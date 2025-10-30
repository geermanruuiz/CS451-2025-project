package cs451;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

public class Pl extends Sl {

    //private Fll fll;
    private List<Pair<Integer, Integer>> sent;
    Timer timer;
    private final Object mutex = new Object();

    public Pl(int id, int port, InetAddress address, String outputPath, HashMap<Integer, Integer> hosts) {
        super(id, port, address, outputPath, hosts);
        // fll = new Fll(id, port, address, outputPath, hosts);
        sent = new ArrayList<>();
    }

    public int deliver(int sender, int msg) {
        Pair<Integer, Integer> pair = new Pair<>(sender, msg);
        synchronized (mutex) {
            if (delivered.contains(pair)) {
                return -1;
            }
            return super.deliver(sender, msg);
        }
    }

    public void send(int receiverPort, int msg) {
        super.send(receiverPort, msg);
        sent.add(new Pair<>(receiverPort, msg));
    }
}
