package Broker;
import Info.InfoBroker;
import Info.InfoConsumer;
import Info.InfoPublisher;
import Utils.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class PublisherThread extends Thread{

    Socket connection;
    ObjectInputStream in;
    ObjectOutputStream out;
    Broker b;

    PublisherThread(Socket connection, Broker b, ObjectOutputStream output, ObjectInputStream input) {
        this.connection=connection;
        out=output;
        in=input;
        this.b=b;
    }

    public void run() {
        ArrayList<InfoBroker> temp = new ArrayList<InfoBroker>();
        ArrayList<InfoBroker> infoForBrokers = new ArrayList<InfoBroker>();
        try {
            String message = (String) in.readObject();
            System.out.println(message);
            if (message.equals("INFO")){
                for (Broker br: b.getBrokers()){
                    temp.add(br.getInfo());
                }
               temp.add(b.getInfo());
               out.writeObject(temp);
               out.flush();

            }
            if (message.equals("UPDATE TOPICS")){

                infoForBrokers = (ArrayList<InfoBroker>) in.readObject();
                for (InfoBroker br : infoForBrokers){
                    System.out.println(br.toString());
                }
                InfoPublisher publisher=(InfoPublisher) in.readObject();
                System.out.println(b.registeredPublishers.size());

                int index=-1;
                int i =0;

                while(i<b.registeredPublishers.size()){
                    if(b.registeredPublishers.get(i).getIp().equals(publisher.getIp()) && b.registeredPublishers.get(i).getPort()==(publisher.getPort())){
                        index=i;
                        break;
                    }else{
                        i++;
                    }
                }

                b.registeredPublishers.set(index,publisher);

                for (InfoBroker info: infoForBrokers){
                    for(Broker brok: b.getBrokers()) {
                        if(((brok.getInfo().getIp()).equals(info.getIp())) && ((brok.getInfo().getPort())==(info.getPort()))){
                            for (String str : info.getTopics()){
                                if(!brok.registeredTopics.contains(str)){
                                    brok.registeredTopics.add(str);
                                }
                            }
                        }
                    }
                }
                for (InfoBroker info : infoForBrokers){
                    if(((b.getInfo().getIp()).equals(info.getIp())) && ((b.getInfo().getPort())==(info.getPort()))){
                        for (String str : info.getTopics()){
                            if(!b.registeredTopics.contains(str)){
                                synchronized (b.registeredTopics) {
                                    b.registeredTopics.add(str);
                                }
                            }
                        }
                    }
                }
                for (Broker bro: b.getBrokers()) {
                    System.out.println(bro.getInfo().toString());
                }
                System.out.println(b.getInfo().toString());
                System.out.println("Done Update Topics");
            }
            if (message.equals("Subscription Update")){
                InfoPublisher pub = (InfoPublisher) in.readObject();
                List<String> hashtags=(List<String>)in.readObject();
                out.writeObject("SEND");
                out.flush();
                ArrayList<byte[]> videoBytes = new ArrayList<>();
                int size=(Integer)in.readObject();
                byte[] bytes;
                for(int i =0 ;i<size;i++){
                    bytes=(byte[]) in.readObject();
                    videoBytes.add(bytes);
                }System.out.println("Video received. ");
                Pair pair = new Pair(hashtags, videoBytes);
                for (InfoConsumer con: b.registeredConsumers){
                    System.out.println(con.toString());
                }

                ArrayList<InfoConsumer> tempConsumers = new ArrayList<>();
                for (InfoConsumer consumer : b.registeredConsumers){
                    for (String s : pair.getTags()){
                        if (consumer.getTopics().contains(s) && !tempConsumers.contains(consumer)){
                            tempConsumers.add(consumer);
                        }
                    }
                }

                for (InfoConsumer consumer : tempConsumers){
                    System.out.println(consumer.getPort());
                    if(!(pub.getPort()==consumer.getPort()-1 && pub.getIp().equals(consumer.getIp()))){
                        b.sendToConsumer(consumer, pair);
                    }
                }
            }

            if (message.equals("SENDING MY INFO")){
                InfoPublisher publisher=(InfoPublisher) in.readObject();
                b.registeredPublishers.add(publisher);

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        finally {
            try {
                in.close();
                out.close();
                connection.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}

