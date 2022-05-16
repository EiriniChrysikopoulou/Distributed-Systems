package Utils;



/*import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TIFF;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;*/
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoFile implements Serializable {
    String videoPath;
    String videoName;
    String channelName;
    String dateCreated;
    String length;
    String framerate;
    String frameWidth;
    String frameHeight;
    public List<String> associatedHashtags;
    byte[] videoFileChunk;
    ArrayList<Chunk> chunks;

    public VideoFile(){};


    public VideoFile(String videoPath,String[] str) throws IOException, SAXException {
        this.videoPath=videoPath;
        File file = new File(videoPath);
        /*Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(file);
        ParseContext context = new ParseContext();
        parser.parse(inputstream, handler, metadata, context);

        dateCreated=metadata.get(Metadata.DATE);
        length =metadata.get(TIFF.IMAGE_LENGTH);
        frameWidth=metadata.get(TIFF.IMAGE_WIDTH);
        frameHeight=null;
        framerate=null;*/
        videoName=file.getName().replace(".mp4","");
        channelName=null; //prosorina null
        associatedHashtags= Arrays.asList(str);
        chunks=new ArrayList<>();
        for(int i=0;i<size();i++){
            chunks.add(new Chunk(associatedHashtags,getVideoFileChunk(i)));
        }
    }

    //copy constructor
    public VideoFile(VideoFile v){
        this.videoPath = v.videoPath;
        this.videoName = v.videoName;
        this.channelName = v.channelName;
        this.dateCreated = v.dateCreated;
        this.length = v.length;
        this.framerate = v.framerate;
        this.frameWidth = v.frameWidth;
        this.frameHeight = v.frameHeight;
        this.associatedHashtags = v.associatedHashtags;
        this.videoFileChunk = v.videoFileChunk;
    }

    public void printFields(){
        System.out.println(videoName+"\n"+ dateCreated+"\n"+ length +"\n"+frameWidth);

    }

    public String getVideoPath(){
        return videoPath;
    }

    public byte[] getVideoFileChunk(int i) throws IOException {

        int chunkSize = 524288; // 512 KB

        File file = new File(videoPath);

        byte[] Mp4 = Files.readAllBytes(file.toPath());
        int index = i*chunkSize;

        int total=Mp4.length/chunkSize;
        int last=Mp4.length-total*chunkSize;

        videoFileChunk = new byte[chunkSize];
        int j = 0;
        int size=chunkSize;


        boolean  flag = false;
        while((j < chunkSize && !flag)){
            videoFileChunk[j] = Mp4[index];

            index++;
            j++;
            if(index==Mp4.length){
                size=last;
                flag=true;
            }
        }
        return videoFileChunk;
    }

    void setVideoFileChunk(byte[] bytes){
        videoFileChunk=bytes;
    }

    public void setLength(float length) {
    }

    public List<String> getAssociatedHashtags() {
        return associatedHashtags;
    }

    public String getVideoName() {
        return videoName;
    }


    public int size() throws IOException {
        int chunkSize=524288;
        File file = new File(videoPath);
        byte[] Mp4 = Files.readAllBytes(Paths.get(videoPath));
        return  (int) Math.ceil((float) Mp4.length/chunkSize);
    }
}

