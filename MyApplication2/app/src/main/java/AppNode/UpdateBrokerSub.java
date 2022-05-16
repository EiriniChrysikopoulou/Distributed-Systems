package AppNode;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Info.InfoConsumer;

public class UpdateBrokerSub extends Thread {

    ObjectOutputStream out=null;
    ObjectInputStream in=null;
    Socket request;
    InfoConsumer consumer;

    public UpdateBrokerSub(Socket request, ObjectInputStream in, ObjectOutputStream out,InfoConsumer consumer){
        this.request=request;
        this.in=in;
        this.out=out;
        this.consumer=consumer;
    }

    public void run() {
        try {
            out.writeObject("Consumer");
            out.flush();
            out.writeObject("ADD SUBSCRIPTION");
            out.flush();
            out.writeObject(consumer);
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