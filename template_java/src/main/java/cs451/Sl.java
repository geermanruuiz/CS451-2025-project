package cs451;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

public class Sl extends Fll {

    //private Fll fll;
    private List<Pair<Integer, Integer>> sent;
    Timer timer;
    int numberOfMessages;
    int currentSet;
    int numItemsPerSet;


    public Sl(int id, int port, InetAddress address, String outputPath, HashMap<Integer, Integer> hosts, int numberOfMessages) {
        super(id, port, address, outputPath, hosts);
        // fll = new Fll(id, port, address, outputPath, hosts);
        sent = new ArrayList<>();
        this.numberOfMessages = numberOfMessages;
        currentSet = 0;
        numItemsPerSet = numberOfMessages / 2;
    }

    public void startTimer(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resendAll();
            }
        }, 1000, 2000);
    }

    public boolean deliver(int sender, int msg) {
        return super.deliver(sender, msg);
    }

    public void send(int receiverPort, int msg) {
        super.send(receiverPort, msg);
        sent.add(new Pair<>(receiverPort, msg));
    }

    private void resendAll() {

        int index = currentSet * numItemsPerSet;
        int endIndex = Math.min(index + numItemsPerSet, sent.size());
        List<Pair<Integer, Integer>> subList = sent.subList(index, endIndex);

        for (Pair<Integer, Integer> p : subList) {
            int receiverPort = p.getKey();
            int msg = p.getValue();
            super.send(receiverPort, msg);
        }
        if (currentSet >= 1) {  // only 2 sets (0 and 1)
            currentSet = 0;
        } else {
            currentSet++;
        }
        System.out.println(sent.size() + " messages sent so far.");
    }
}
