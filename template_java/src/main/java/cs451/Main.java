package cs451;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.HashMap;

public class Main {

    private static void handleSignal() {

        // immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        // write/flush output file if necessary
        System.out.println("Writing output.");
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        Parser parser = new Parser(args);
        parser.parse();

        initSignalHandlers();

        // example
        long pid = ProcessHandle.current().pid();
        System.out.println("My PID: " + pid + "\n");
        System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid
                + "` to stop processing packets\n");

        System.out.println("My ID: " + parser.myId() + "\n");

        // -------------------------CONFIG---------------------------------
        System.out.println("Leyendo la configuración: " + parser.config());
        int num_msg = -1, receptor = -1;

        try (BufferedReader br = new BufferedReader(new FileReader(parser.config()))) {
            String linea = br.readLine(); // leer la primera línea
            if (linea != null) {
                String[] partes = linea.trim().split("\\s+"); // dividir por espacios
                num_msg = Integer.parseInt(partes[0]);
                receptor = Integer.parseInt(partes[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Numero de mensajes a enviar: " + num_msg);
        System.out.println("Id del receptor: " + receptor);
        System.out.println("==========================");
        // ----------------------------------------------------------------------

        System.out.println("List of resolved hosts is:");
        System.out.println("==========================");

        Integer port = -1;
        InetAddress address;

        int receptorPort = -1;

        try {
            address = InetAddress.getByName("localhost");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HashMap<Integer, Integer> hostsMap = new HashMap<>();

        for (Host host : parser.hosts()) {

            if (host.getId() == parser.myId()) {
                port = host.getPort();
                try {
                    address = InetAddress.getByName(host.getIp());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (host.getId() == receptor) {
                receptorPort = host.getPort();
            }

            hostsMap.put(host.getPort(), host.getId());
            System.out.println(host.getId());
            System.out.println("Human-readable IP: " + host.getIp());
            System.out.println("Human-readable Port: " + host.getPort());
            System.out.println();
        }
        System.out.println();

        System.out.println("Path to output:");
        System.out.println("===============");
        System.out.println(parser.output() + "\n");

        System.out.println("Doing some initialization\n");

        if (port == -1) {
            System.out.println("Port is null");
            return;
        } else {
            if (receptor == parser.myId()) {
                // lanzo receptor
                Sl recep = new Pl(parser.myId(), port, address, parser.output(), hostsMap);
                    while (true) {
                        DatagramPacket packet = recep.receive();
                        String received_string = new String(packet.getData(), 0, packet.getLength());
                        int receivedMsg = Integer.parseInt(received_string);

                        // Creo thread que hace el handle y el deliver
                        Thread receiverThread = new Thread( () -> {
                            System.out.println("Handling received message!");
                            int checkDeliver = recep.deliver(packet.getPort(), receivedMsg);
                            if(checkDeliver == 0) {
                                System.out.println("Message " + receivedMsg + " has been delivered.");
                                recep.writeReceive(packet.getPort(), receivedMsg);
                            }
                        });
                        receiverThread.start();
                    }

            } else {
                // lanzo escritor
                System.out.println("Creating a writer...");
                Sl writer = new Pl(parser.myId(), port, address, parser.output(), hostsMap);
                System.out.println("Sending messages!");
                writer.startTimer();
                for(int msg = 1; msg < num_msg+1; msg++){
                    writer.send(receptorPort, msg);
                    writer.writeSend(msg);
                    writer.deliver(writer.getMyId(), msg);
                }
            }
        }

        System.out.println("Broadcasting and delivering messages...\n");

        // After a process finishes broadcasting,
        // it waits forever for the delivery of messages.
        while (true) {
            // Sleep for 1 hour
            Thread.sleep(60 * 60 * 1000);
        }
    }
}
