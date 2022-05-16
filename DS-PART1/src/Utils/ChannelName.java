package Utils;

import java.util.ArrayList;

public class ChannelName {
    String channelName;
    ArrayList<String> hashtagsPubllished;

    public ChannelName(String channelName){
        this.channelName=channelName;
        hashtagsPubllished=new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }
}
