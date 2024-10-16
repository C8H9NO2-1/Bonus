package fr.esisar.bonus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class MD5 extends Thread {
    private Integer value;
    private Integer port;

    private int valueToTest;
    private int end;
    private String salt;
    private String hash;
    private DatagramSocket socket;
    private InetSocketAddress adrDest;

    public MD5(int valueToTest, int end, String salt, String hash, DatagramSocket socket, InetSocketAddress adrDest) {
        //System.out.println("valueToTest = " + valueToTest);
        //System.out.println("end = " + end);
        this.valueToTest = valueToTest;
        this.end = end;
        this.salt = salt;
        this.hash = hash.toUpperCase();
        this.socket = socket;
        this.adrDest = adrDest;
    }

    @Override
    public void run() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            for (int i = valueToTest; i < end; i++) {
                String mdp = String.format("%09d", i);
                //System.out.println(mdp);
                byte[] hashD = digest.digest((salt + mdp).getBytes());

                StringBuilder sb = new StringBuilder();
                for (byte b: hashD) {
                    sb.append(String.format("%02X", b));
                }

                if (sb.toString().equals(hash)) {
                    byte[] bufE = new String("FOUND" + mdp).getBytes();
                    DatagramPacket dpE = new DatagramPacket(bufE, bufE.length, adrDest);
                    socket.send(dpE);
                    System.out.println("Solution found");
                }

                //System.out.println(hash);
                //System.out.println(sb.toString());
            }
        } catch (Exception e) {
            System.out.println("This is pretty bad");
        }
        //InetSocketAddress adrDest;
        //OutputStream os;
        //InputStream is;
        //byte[] bufE;
        //byte[] bufR = new byte[2048];
        //int lenBufR;
        //String reponse;
        //adrDest = new InetSocketAddress("127.0.0.1", port);
        //reponse = new String();
        //try {
        //    Socket socket = new Socket();
        //    socket.connect(adrDest);
        //    bufE = new String("COMBIEN").getBytes();
        //    os = socket.getOutputStream();
        //    os.write(bufE);
        //
        //    is = socket.getInputStream();
        //    lenBufR = is.read(bufR);
        //
        //    while (lenBufR != -1) {
        //        reponse += new String(bufR, 0, lenBufR);
        //        lenBufR = is.read(bufR);
        //    }
        //
        //    int i = reponse.indexOf('=');
        //    int j = reponse.indexOf('E');
        //
        //    value = Integer.parseInt(reponse.substring(i + 1, j));
        //
        //    socket.close();
        //} catch (Exception e) {
        //    System.out.println("Problème lors de la connexion au port: " + port);
        //    System.out.println("Erreur dans un thread: " + e);
        //    return;
        //}
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("Début de la recherche...");
        MessageDigest digest = MessageDigest.getInstance("MD5");
        String mdp = String.format("%09d", 42);
        System.out.println(mdp);
        byte[] hashD = digest.digest(("tototiti" + mdp).getBytes());
        System.out.println("tototiti" + mdp);

        StringBuilder sb = new StringBuilder();
        for (byte b: hashD) {
            sb.append(String.format("%02X", b));
        }

        System.out.println(sb.toString());
        //
        DatagramSocket socket = new DatagramSocket();

        // Creation et envoi du message
        InetSocketAddress adrDest = new InetSocketAddress("127.0.0.1", 5000);
        byte[] bufE = new String("REQUEST").getBytes();
        DatagramPacket dpE = new DatagramPacket(bufE, bufE.length, adrDest);
        socket.send(dpE);
        System.out.println("Message envoyé");

        // Attente de la reponse 
        byte[] bufR = new byte[2048];
        DatagramPacket dpR = new DatagramPacket(bufR, bufR.length);
        socket.receive(dpR);
        String reponse = new String(bufR, dpR.getOffset(), dpR.getLength());
        System.out.println("Reponse recue = "+reponse);

        String[] reponseTemp = reponse.split(",");

        int begin = Integer.parseInt(reponseTemp[2]);
        int end = Integer.parseInt(reponseTemp[3]);
        String salt = reponseTemp[1];
        String hash = reponseTemp[0];
        System.out.println("begin = " + begin + " a");
        System.out.println("end = " + end + " a");
        System.out.println("salt = " + salt + " a");
        System.out.println("hash = " + hash + " a");
        int numberOfThread = 10000;
        int step = (end - begin) / numberOfThread;
        //
        List<MD5> threads = new ArrayList<>();
        //
        //int finalMax = 0;
        //int finalPort = 0;
        //int finalSum = 0;
        //

        for (int i = 0; i < numberOfThread; i++) {
            int temp = begin + (i * step);
            threads.add(new MD5(temp, temp + step, salt, hash, socket, adrDest));
            threads.get(i).start();
        } 
        //
        for (MD5 thread: threads) {
            thread.join();
        }
        //
        //
        //for (SommeTCP thread: threads) {
        //    if (thread.value > finalMax) {
        //        finalMax = thread.value;
        //        finalPort = thread.port;
        //    }
        //
        //    finalSum += thread.value;
        //}
        //
        //System.out.println("Le montant maximum est " + finalMax + " euros");
        //System.out.println("Le port d'écoute correspondant à ce maximum est " + finalPort);
        //System.out.println("La somme des montants retournés par tous les ports est " + finalSum);
        long stop = System.currentTimeMillis();
        System.out.println("Elapsed Time = "+(stop-start)+" ms");
        
    }
}
