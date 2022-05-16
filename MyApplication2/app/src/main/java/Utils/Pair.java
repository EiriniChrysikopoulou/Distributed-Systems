package Utils;

import java.util.ArrayList;
import java.util.List;

public class Pair {

    List<String> tags=new ArrayList<>();
    ArrayList<byte[]> bytes=new ArrayList<>();

    public Pair (List<String> tags,  ArrayList<byte[]> bytes){
        this.tags=tags;
        this.bytes=bytes;

    }

    public List<String> getTags() {
        return tags;
    }

    public ArrayList<byte[]> getBytes(){
        return bytes;
    }
}