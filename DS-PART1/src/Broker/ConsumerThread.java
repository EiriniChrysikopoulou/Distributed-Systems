package Broker;

import Info.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConsumerThread extends Thread{
    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    Broker b;
    ArrayList<ArrayList<byte[]>> videos = new ArrayList<>();

    ConsumerThread(Socket connection, Broker b , ObjectOutputStream output, ObjectInputStream input) {
        this.connection=connection;
        out=output;
        in=input;
        this.b=b;
    }

    public void run() {
        try {

            String a = (String) in.readObject();
            System.out.println(a);

            if (a.equals("SENDING MY INFO")){
                InfoConsumer info = (InfoConsumer) in.readObject();
                if (!b.registeredConsumers.contains(info)){
                    synchronized (b.registeredConsumers){
                        b.registeredConsumers.add(info);
                    }
                }
            }

            if(a.equals("Get topics")){
                ArrayList<String> topics = new ArrayList<>();
                for (InfoPublisher p: b.registeredPublishers){
                    for(String s: p.getHashTags()){
                        if(!topics.contains(s)){
                            topics.add(s);
                        }
                    }
                }
                out.writeObject(topics);
                out.flush();
            }

            if(a.equals("ADD SUBSCRIPTION")){
                InfoConsumer consumer=(InfoConsumer) in.readObject();
                for(InfoConsumer infoConsumer:b.registeredConsumers){
                    if(infoConsumer.getIp().equals(consumer.getIp()) && infoConsumer.getPort()==consumer.getPort()){
                        synchronized (b.registeredConsumers){
                            infoConsumer.setTopics(consumer.getTopics());
                        }
                        for (String s :infoConsumer.getTopics()){
                            System.out.println(s);
                        }
                    }
                }
            }

            if (a.equals("SEARCH TOPIC")){
                InfoConsumer consumer=(InfoConsumer) in.readObject();
                String topic = (String) in.readObject();

                boolean flag=false;
                List<Broker> toSearch=b.getBrokers();
                toSearch.add(b);

                for(Broker br: toSearch){
                    if(br.isResponsible(topic)){
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    out.writeObject("NO VIDEO FOUND");
                    out.flush();
                }
                else{
                    out.writeObject("VIDEO FOUND");
                    out.flush();
                    System.out.println("Searching consumer's topic..");
                    if (b.isResponsible(topic)){
                        out.writeObject("RESPONSIBLE");
                        out.flush();
                        System.out.println("RESPONSIBLE");
                        for (InfoPublisher p: b.registeredPublishers){
                            if(p.getHashTags().contains(topic) && !(p.getPort()==consumer.getPort()-1 && p.getIp().equals(consumer.getIp()))){
                                System.out.println("Sending to publisher socket: "+p.getPort());
                                Socket socket = new Socket("127.0.0.1", p.getPort());
                                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                                output.writeObject("NEED VIDEOS");
                                output.flush();
                                output.writeObject(topic);
                                output.flush();
                                while (!((String) input.readObject()).equals("Done")) {
                                    int size = (Integer) input.readObject();
                                    ArrayList<byte[]> videoBytes = new ArrayList<>();
                                    byte[] bytes;
                                    for(int i =0 ;i<size;i++){
                                        bytes=(byte[]) input.readObject();
                                        videoBytes.add(bytes);
                                    }
                                    videos.add(videoBytes);
                                    System.out.println("Requested video received. ");
                                }
                            }
                        }
                        for (ArrayList<byte[]> video: videos){
                            out.writeObject("Sending requested videos");
                            out.flush();
                            out.writeObject(video.size());
                            out.flush();
                            for (byte[] bytes : video) {
                                out.writeObject(bytes);
                                out.flush();
                            }
                        }
                        out.writeObject("Done");
                        out.flush();
                    }
                    else{
                        out.writeObject("NOT RESPONSIBLE");
                        out.flush();
                        System.out.println("NOT RESPONSIBLE");
                        for (Broker br: b.getBrokers()){
                            if(br.isResponsible(topic)){
                                out.writeObject(br.ip);
                                out.flush();
                                out.writeObject(br.port);
                                out.flush();

                            }
                        }
                    }
                }
            }
        } catch(IOException | ClassNotFoundException ioException){
            ioException.printStackTrace();
        }

    }
}