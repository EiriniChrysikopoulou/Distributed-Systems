package Info;

import java.io.Serializable;
import java.util.ArrayList;

import Utils.PublisherPair;
import Utils.VideoFile;

public class InfoPublisher implements Serializable {

    String ip;
    int port;
    String channelName;
    ArrayList<String> hashTags=new ArrayList<>();
    public static ArrayList<PublisherPair>videos=new ArrayList<>();
    ArrayList<InfoBroker> brokers=new ArrayList<>();


    public InfoPublisher(String ip,int port,String channelName,ArrayList<String> hashTags,ArrayList<InfoBroker> brokers){
        this.ip=ip;
        this.port=port;
        this.channelName=channelName;
        this.hashTags=hashTags;
        this.brokers=brokers;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getChannelName() {
        return channelName;
    }

    public ArrayList<String> getHashTags() {
        return hashTags;
    }

    public void addPair(PublisherPair v){
        videos.add(v);
    }

    public ArrayList<PublisherPair> getVideos(){return videos;}

    public void addTopic(String t){
        hashTags.add(t);
    }

    public void setBrokers(ArrayList<InfoBroker> b){
        brokers=b;
    }
    public ArrayList<InfoBroker> getBrokers(){
        return brokers;
    }


}
