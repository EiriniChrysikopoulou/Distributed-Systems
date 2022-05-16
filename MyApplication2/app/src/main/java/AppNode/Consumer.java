package AppNode;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.myapplication.BrokerActivity;
import Info.*;
import Utils.ChannelName;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Consumer {

    ArrayList<InfoBroker> Broker_list = new ArrayList<>();
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    Socket requestSocket = null;
    String IP;
    ChannelName name;
    int port;
    ArrayList<String> registeredTopics = new ArrayList<>();
    String BrokerIP;
    int BrokerPort;
    Random rand = new Random();
    int counter= rand.nextInt(1000);

    public Consumer() {
    }

    public Consumer(String x, int y, ChannelName z) {
        this.IP = x;
        this.port = y;
        this.name = z;
    }

    public String getIP() {
        return IP;
    }

    public String getName() {
        return name.getChannelName();
    }

    public int getPort() {
        return port;
    }

    public void init(String ip, int port, Context con) throws IOException, ClassNotFoundException, InterruptedException {

        BrokerIP = ip;
        BrokerPort = port;
        requestSocket = new Socket(BrokerIP, BrokerPort);
        out = new ObjectOutputStream(requestSocket.getOutputStream());
        in = new ObjectInputStream(requestSocket.getInputStream());
        out.writeObject("NEED BROKER INFO");
        out.flush();
        ArrayList<InfoBroker> info = (ArrayList<InfoBroker>) in.readObject();
        Broker_list = info;

        for (InfoBroker b : Broker_list){
            Socket socket = new Socket(b.ip, b.port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("Consumer");
            out.flush();
            out.writeObject("SENDING MY INFO");
            out.flush();
            out.writeObject(getInfo());
            out.close();
            in.close();
            socket.close();
        }

        out.close();
        in.close();
        requestSocket.close();

        SubTask subTask=new SubTask();
        subTask.execute(con);
    }

    public void consumerPrint(){
        if(Broker_list.isEmpty()){
            Log.e("Brokers_consumer","empty");
        }
        else
            Log.e("Brokers_consumer","received");
        for(InfoBroker b : Broker_list){
            Log.e("Broker_consumer", String.valueOf(b.port));
        }
    }


    public InfoConsumer getInfo(){
        return new InfoConsumer(IP,port,name.getChannelName(),registeredTopics,Broker_list);
    }

    private class SubTask extends AsyncTask<Context,Void,Void> {
        @Override
        protected Void doInBackground(Context...context) {
            Random rand = new Random();
            int counter= rand.nextInt(1000);
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            ServerSocket s = null;
            Context app_context = context[0];
            try {
                s = new ServerSocket(port);
            while (true) {
                Log.e("Accepted Connection","Ok");
                    Socket connection = s.accept();
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

                    out.writeObject("Subscription Connection accepted");
                    out.flush();
                    while (!((String) in.readObject()).equals("Done")) {
                        int size = (Integer) in.readObject(); // posa chunks 8a er8oun
                        ArrayList<byte[]> v = new ArrayList<>();
                        byte[] tempVid;
                        for (int i = 0; i < size; i++) {
                            tempVid = (byte[]) in.readObject();
                            v.add(tempVid);
                        }
                        File file = new File(app_context.getExternalFilesDir(null).getAbsolutePath() + File.separator + "Publish"); //We create a new folder named "Publish"
                        file.mkdir();
                        File newFile = new File (file.getAbsolutePath() + File.separator + counter + ".mp4");
                        FileOutputStream out1 = new FileOutputStream(newFile);
                        
                        for (byte[] b : v) {
                            out1.write(b);
                        }
                        out1.close();
                        counter++;

                        String p = newFile.getAbsolutePath();
                        Log.e("path", p );
                        Log.e("Subscription saved.", "ok" );
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    assert in != null;
                    in.close();
                    out.close();
                    s.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
            return null;
        }
    }
}