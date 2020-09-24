import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static int port = 9002;

    static String sendString = "sbsloc"; // SoundBoardServerLocate
    static String ack = "ack";
    public static void main(String[] args) throws IOException {
        String inp = "";

        System.out.print("send or recv: ");
        Scanner reader = new Scanner(System.in);
        inp = reader.nextLine();

        switch (inp){
            case "recv" -> startBroadcastListener();
            case "send" -> {
                List<InetAddress> addrs = discoveryBroadcast();

                // Output all the addrs
                System.out.println("Server Addresses: ");
                addrs.forEach(System.out::println);
            }
            default -> System.out.println("Invalid option.");
        }
    }

    static void startBroadcastListener() throws IOException {
        // Create port
        DatagramSocket sock = new DatagramSocket(port);

        // Form the packets/data objs
        byte[] recvData = new byte[32];
        byte[] sendData = ack.getBytes(StandardCharsets.UTF_8);

        DatagramPacket recvPack = new DatagramPacket(recvData,
                recvData.length);

        // Run the check.
        System.out.println("Waiting for incoming broadcasts.");
        while(true){
            // Receive data
            sock.receive(recvPack);
            String sentance = new String(recvPack.getData(), 0, recvPack.getLength());

            System.out.println("Received: " + sentance + "\nFrom: " + recvPack.getAddress());

            // Send acknowledgement if it is the valid check string.
            DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, recvPack.getAddress(), recvPack.getPort());
            sock.send(sendPack);
        }
        // Needs an end/pause clause in development system
        // sock.close();
    }

    static List<InetAddress> discoveryBroadcast() throws IOException {
        // Create broadcast socket
        DatagramSocket sock = new DatagramSocket();
        sock.setSoTimeout(5000);
        sock.setBroadcast(true);

        // Form the broadcast packet
        byte[] buff = sendString.getBytes();

        DatagramPacket packet = new DatagramPacket(buff, buff.length, InetAddress.getByName("255.255.255.255"), port);

        // Send the broadcast packet
        sock.send(packet);

        // Form the reception packet obj
        byte[] recvData = new byte[8];
        DatagramPacket recvPack = new DatagramPacket(recvData, recvData.length);

        // Loop for 7.5 seconds (or till there the is a Timeout after 7.5s and collate all incoming acks
        long t= System.currentTimeMillis();
        long end = t+7500; // end in 7.5 seconds

        // List of all the acks addresses.
        List<InetAddress> acks = new ArrayList<>();

        while(System.currentTimeMillis() < end) {
            try {
                sock.receive(recvPack);

                acks.add(recvPack.getAddress());

            } catch (SocketTimeoutException ignored) {

            }
        }

        // Close the socket
        sock.close();

        return acks;
    }
}
