package AppNode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Info.InfoBroker;
import Info.InfoPublisher;

public class UpdateBrokers extends Thread {

    ObjectOutputStream out;
    ObjectInputStream in;
    Socket request;
    ArrayList<InfoBroker> infoForBrokers;
    InfoPublisher publisher;

    public UpdateBrokers(Socket request, ObjectInputStream in, ObjectOutputStream out, ArrayList<InfoBroker> infoForBrokers,InfoPublisher publisher){
        this.request=request;
        this.in=in;
        this.out=out;
        this.infoForBrokers=infoForBrokers;
        this.publisher=publisher;
    }
    public void run() {
        try {
            out.writeObject("Publisher");
            out.flush();
            out.writeObject("UPDATE TOPICS");
            out.flush();
            out.writeObject(infoForBrokers);
            out.flush();
            out.writeObject(publisher);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                request.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

}