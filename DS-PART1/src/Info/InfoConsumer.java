package Info;

import java.io.Serializable;
import java.util.ArrayList;

public class InfoConsumer implements Serializable {

    String ip,name;
    int port;
    ArrayList<String> topics=new ArrayList<>();
    ArrayList<InfoBroker> brokers=new ArrayList<>();

    public InfoConsumer(String ip,int port,String name,ArrayList<String> topics,ArrayList<InfoBroker> brokers){
        this.ip=ip;
        this.port=port;
        this.name=name;
        this.topics=topics;
        this.brokers=brokers;
    }

    public void setTopics(ArrayList<String> topics) {
        this.topics = topics;
    }

    public String getIp(){
        return ip;
    }

    public int getPort(){
        return port;
    }

    public String getName(){
        return name;
    }

    public ArrayList<String> getSub(){
        return topics;
    }

    public String toString(){
        return  (ip+" "+port+" "+name);
    }

    public ArrayList<String> getTopics() {
        return topics;
    }

    public void addTopic(String s ){
        topics.add(s);
    }
    public ArrayList<InfoBroker> getBrokers(){
        return brokers;
    }
}
