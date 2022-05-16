package AppNode;

import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.xml.sax.SAXException;

import Info.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
/*import java.util.Scanner;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;*/

import Utils.ChannelName;
import Utils.PublisherPair;
import Utils.VideoFile;

public class Publisher {


    public String ip;
    public int port;

    public ArrayList<String> getHashtags() {
        return hashtags;
    }
    private ArrayList<String> hashtags = new ArrayList<String>();
    private ArrayList<InfoBroker> Infobrokers = new ArrayList<InfoBroker>();

    public ArrayList<VideoFile> videos = new ArrayList<VideoFile>();
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket requestSocket = new Socket();
    ChannelName channelName;

    public Publisher(){};

    public Publisher(ChannelName x,String ip,int port){
        this.channelName = x;
        this.ip=ip;
        this.port=port;
        Log.e("New publisher", "new publisher");
    }

    public InfoPublisher getInfo() {
        return new InfoPublisher(ip, port,channelName.getChannelName(), hashtags,Infobrokers);
    }

    public void getBrokerList(int port) {
        Socket requestSocket;
        ObjectOutputStream out;
        ObjectInputStream in;
        String temp_Ip;
        int temp_port;

        try {
            requestSocket = new Socket("192.168.56.1", port);
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());

            out.writeObject("Publisher");
            out.flush();
            out.writeObject("INFO");
            out.flush();
            ArrayList<InfoBroker> temp = (ArrayList<InfoBroker>) in.readObject();
            Infobrokers=temp;

        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException cnf) {
            System.out.println("Class not found exception thrown.");
        }
    }

    public void sendPublisherInfo() throws IOException {
        for(InfoBroker b : Infobrokers){
            Socket socket = new Socket(b.ip, b.port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("Publisher");
            out.flush();
            out.writeObject("SENDING MY INFO");
            out.flush();
            out.writeObject(this.getInfo());
            out.flush();
        }
    }
}
