package Utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class PublisherPair implements Serializable {

    String[] tags;
    String videoPath;

    public PublisherPair (String[] tags,  String videoPath){
        this.tags=tags;
        this.videoPath=videoPath;

    }

    public String[] getTags() {
        return tags;
    }

    public List<String> topicList () {
        return Arrays.asList(tags);
    }

    public String getPath(){
        return videoPath;
    }
}
