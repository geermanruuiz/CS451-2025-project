package cs451;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

public class Pl extends Sl {

    //private Fll fll;
    private List<Pair<Integer, Integer>> sent;
    private int numberOfProcesses;

    public Pl(int id, int port, InetAddress address, String outputPath, HashMap<Integer, Integer> hosts, int numberOfMessages,int numberOfProcesses) {
        super(id, port, address, outputPath, hosts, numberOfMessages);
        // fll = new Fll(id, port, address, outputPath, hosts);
        sent = new ArrayList<>();
        this.numberOfProcesses = numberOfProcesses;
    }

    public boolean deliver(int sender, int msg) {
        System.out.println(delivered.size() + " messages delivered so far.");
        return super.deliver(sender, msg);
    }

    public void send(int receiverPort, int msg) {
        super.send(receiverPort, msg);
        sent.add(new Pair<>(receiverPort, msg));
    }

    public boolean verifyAllDelivered(int numProcesses, int numMessages) {
        // Track missing messages (if any)
        List<Pair<Integer, Integer>> missing = new ArrayList<>();

        // 1. Check all expected messages exist
        for (int pid = 0; pid < numProcesses; pid++) {
            for (int mid = 1; mid < numMessages; mid++) { // from 1 to N-1
                Pair<Integer, Integer> expected = new Pair(pid, mid);
                if (!delivered.contains(expected)) {
                    missing.add(expected);
                }
            }
        }

        // 2. Optionally, check for unexpected/extra messages
        int expectedCount = numProcesses * (numMessages - 1);
        if (delivered.size() > expectedCount) {
            System.err.println("⚠️ Found extra messages in delivered set: "
                    + (delivered.size() - expectedCount));
        }

        // 3. Report results
        if (missing.isEmpty() && delivered.size() == expectedCount) {
            System.out.println("✅ All messages were delivered exactly once.");
            return true;
        } else {
            System.err.println("❌ Missing messages: " + missing.size());
            if (missing.size() < 50) { // avoid spamming logs
                missing.forEach(System.err::println);
            }
            return false;
        }
    }
}