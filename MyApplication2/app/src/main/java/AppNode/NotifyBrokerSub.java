package AppNode;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Info.InfoPublisher;
import Utils.VideoFile;

public class NotifyBrokerSub extends Thread{
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket request;
    VideoFile v;
    InfoPublisher publisher;
    public NotifyBrokerSub(Socket request, ObjectInputStream in, ObjectOutputStream out, VideoFile v, InfoPublisher publisher){
        this.request=request;
        this.in=in;
        this.out=out;
        this.v=v;
        this.publisher = publisher;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        try {
            out.writeObject("Publisher");
            out.flush();
            out.writeObject("Subscription Update");
            out.flush();
            out.writeObject(publisher);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.writeObject(v.associatedHashtags);
            out.flush();
            String ans = (String) in.readObject();
            Log.e("Answer",ans);
            if (ans.equals("SEND")) {
                out.writeObject(v.size());
                Log.e("Size", String.valueOf(v.size()));
                out.flush();
                for (int i = 0; i < v.size(); i++) {
                    byte[] temp = v.getVideoFileChunk(i);
                    Log.e("Send Chunk","OK");
                    out.writeObject(temp);
                    out.flush();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
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