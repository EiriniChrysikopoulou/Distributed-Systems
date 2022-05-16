package AppNode;

import android.os.Build;
import android.util.Log;
import androidx.annotation.RequiresApi;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import Info.InfoPublisher;
import Utils.PublisherPair;
import Utils.VideoFile;

public class RequestThread extends Thread{

    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    ServerSocket server = null;
    InfoPublisher p;

    public RequestThread(InfoPublisher p) throws ClassNotFoundException, IOException, SAXException {
        this.p=p;
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void run() {
        try {
            server = new ServerSocket(p.getPort());
            while (true) {
                Socket socket = server.accept();
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                String a = (String) in.readObject();
                if (a.equals("NEED VIDEOS")) {
                    String topic = (String) in.readObject();
                    for (PublisherPair pair : p.getVideos()) {
                        if (pair.topicList().contains(topic)) {
                            out.writeObject("SENDING VIDEOS");
                            out.flush();
                            VideoFile video = new VideoFile(pair.getPath(), pair.getTags());
                            out.writeObject(video.size());
                            out.flush();
                            for (int i = 0; i < video.size(); i++) {
                                byte[] temp = video.getVideoFileChunk(i);
                                out.writeObject(temp);
                                out.flush();
                            }
                            out.writeObject("Done");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | IOException | SAXException e) {
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
                out.close();
                server.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
