package Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Chunk implements Serializable {
   List<String> associatedHashtags=new ArrayList<>();
   byte[] bytes;

   public Chunk(List<String> associatedHashtags, byte[] bytes){
      this.associatedHashtags=associatedHashtags;
      this.bytes=bytes;

   }

   public byte[] getBytes(){
      return bytes;
   }

}

