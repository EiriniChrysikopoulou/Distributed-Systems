package Info;

import Info.*;
import Utils.Utils;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class InfoBroker implements Serializable,Comparable<InfoBroker> {

    private final ArrayList<InfoConsumer> registeredConsumers;
    public String ip;
    public int port, broker_id;
    public ArrayList<String> topics=new ArrayList <String>();
    BigInteger minHash;
    public BigInteger maxHash;

    public InfoBroker(String ip, int port, ArrayList<String> topics, BigInteger minHash, ArrayList<InfoConsumer> registeredConsumers){
        this.ip= ip;
        this.port=port;
        this.topics=topics;
        this.minHash=minHash;
        this.registeredConsumers=registeredConsumers;
        maxHash=Utils.MD5(ip+port);
    }
    public int compareTo(InfoBroker o) {
        this.maxHash=Utils.MD5(ip+port);
        return this.maxHash.compareTo(o.maxHash);
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setBroker_id(int broker_id) {
        this.broker_id = broker_id;
    }

    public void setTopics (ArrayList <String> x) {
        this.topics = x;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public int getBroker_id() {
        return broker_id;
    }

    public ArrayList<InfoConsumer> getRegisteredConsumers(){
        return registeredConsumers;
    }

    public BigInteger getMinHash(){
        return minHash;
    }

    public  ArrayList<String> getTopics(){
        return topics;
    }

    public String TopicstoString(){
        String s;
        if (topics!=null){
            s = topics.toString();
        }
        else {
            s = "No topics";
        }
        return s;
    }

    public String toString(){
        String s= getIp()+" "+getPort()+" "+TopicstoString();
        return s;
    }
}

