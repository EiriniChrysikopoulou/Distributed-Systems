package Broker;

//import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, SAXException {

        System.out.println("Select broker to run:{0,1,2}");
        Scanner sc=new Scanner(System.in);
        String answer=sc.nextLine();
        int b= Integer.parseInt(answer);
        String s = Files.readAllLines(Paths.get("C:\\Users\\Eirini\\Desktop\\DS-PART1\\src\\Node\\brokers.txt")).get(b);
        String [] t=s.split(":",2);
        Broker br = new Broker(t[0], Integer.parseInt(t[1]));
        br.init(b);
        br.connect();
    }
}
