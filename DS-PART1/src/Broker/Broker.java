package Broker;

import Info.*;
import Utils.Pair;
import Utils.Utils;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Broker implements Comparable<Broker>{

    public ArrayList<String> registeredTopics;
    BlockingQueue<ArrayList<byte[]>> queue;
    InfoBroker info;
    static List<Broker> brokers=new ArrayList<Broker>();
    public String ip;
    public int port;
    public ArrayList<InfoConsumer> registeredConsumers;
    List<InfoPublisher> registeredPublishers;
    public BigInteger maxHash;
    public BigInteger minHash;

    public Broker(String ip, int port) {
        maxHash=Utils.MD5(ip+port);
        this.ip = ip;
        this.port = port;
        registeredTopics=new ArrayList<>();
        info=new InfoBroker(ip,port,registeredTopics,minHash,registeredConsumers);
        registeredConsumers=new ArrayList<InfoConsumer>();
        registeredPublishers=new ArrayList<InfoPublisher>();
        queue= new LinkedBlockingQueue<>();
    }

    public void calculateKeys() {
        Collections.sort(brokers);
        BigInteger min;
        for (int i=0;i<brokers.size();i++){
            if(i==0){
                brokers.get(i).minHash=BigInteger.valueOf(0);
                min=BigInteger.valueOf(0);
            }
            else{
                brokers.get(i).minHash=brokers.get(i-1).maxHash.add(BigInteger.valueOf(1));
                min=brokers.get(i-1).maxHash.add(BigInteger.valueOf(1));
            }
        }
    }

    public void init(int a) throws IOException {
        try {
            File myObj = new File("C:\\Users\\Eirini\\Desktop\\DS-PART1\\src\\Node\\brokers.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] temp = data.split(":", 2);
                brokers.add(new Broker(temp[0], Integer.parseInt(temp[1])));
            }
            myReader.close();
            calculateKeys();
            for(Broker b : brokers){
                if(b.ip.equals(ip) && b.port==port){
                    brokers.remove(b);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public List<Broker> getBrokers() throws IOException {
        return brokers;
    }

    public void connect() throws IOException, InterruptedException, ClassNotFoundException {
        ServerSocket brokerSocket = new ServerSocket(port);
        System.out.println("Broker waiting for connections..");
        try {
            while (true) {
                Socket connection = brokerSocket.accept();
                ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
                String message = (String) input.readObject();
                System.out.println(message);


                if (message.equals("NEED BROKER INFO")){
                    ArrayList<InfoBroker> tempBr = new ArrayList<InfoBroker>();
                    for (Broker br : brokers){
                        tempBr.add(br.getInfo());
                    }
                    tempBr.add(this.getInfo());
                    output.writeObject(tempBr);
                    output.flush();
                }

                if (message.equals("Consumer")) {
                    ConsumerThread t = new ConsumerThread(connection, this, output, input);
                    t.start();
                }

                if(message.equals("Publisher")) {
                    PublisherThread t = new PublisherThread(connection, this, output, input);
                    t.start();
                }
            }
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        finally{
            try {
                brokerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public int compareTo(Broker o) {
        this.maxHash=Utils.MD5(ip+port);
        return this.maxHash.compareTo(o.maxHash);
    }

    public InfoBroker getInfo(){
        return new InfoBroker(ip,port,registeredTopics,minHash,registeredConsumers);
    }

    void sendToConsumer(InfoConsumer info, Pair pair) throws IOException, ClassNotFoundException {

            System.out.println(info.getPort());
            Socket request=new Socket("127.0.0.1", info.getPort());
            System.out.println(info.getIp()+" "+info.getPort());
            ObjectOutputStream output = new ObjectOutputStream(request.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(request.getInputStream());
            String ans=(String)input.readObject();
            if(ans.equals("Subscription Connection accepted")) {
                output.writeObject("Sending File");
                output.flush();
                ArrayList<byte[]> temp = pair.getBytes();
                output.writeObject(temp.size());
                output.flush();
                for (byte[] bytes : temp) {
                    output.writeObject(bytes);
                    output.flush();
                }
            }
            output.writeObject("Done");
            output.flush();
    }

    public boolean isResponsible (String x) throws IOException {
        return registeredTopics.contains(x);
    }

    public ArrayList<String> getRegisteredTopics() {
        return registeredTopics;
    }
}
