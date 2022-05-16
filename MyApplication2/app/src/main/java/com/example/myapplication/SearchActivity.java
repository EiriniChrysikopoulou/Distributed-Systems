package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Random;

import Info.InfoConsumer;

public class SearchActivity extends AppCompatActivity {

    Button back_to_menu;
    Button search;
    TextView text;
    EditText input;
    InfoConsumer infoconsumer;
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        back_to_menu=(Button)findViewById(R.id.Back_to_menu) ;
        search=(Button)findViewById(R.id.Search_btn);
        text= (TextView) findViewById(R.id.Search_view);
        input=(EditText)findViewById(R.id.topic_search);
        Intent intent = getIntent();
        infoconsumer = (InfoConsumer) intent.getSerializableExtra("consumerInfo");
    }


    @Override
    protected void onStart() {
        super.onStart();
        back_to_menu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counter= rand.nextInt(1000);
                Socket requestSocket = null;
                try {
                    requestSocket = new Socket("192.168.56.1", 5000);
                    ObjectOutputStream out = new ObjectOutputStream(requestSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(requestSocket.getInputStream());
                    out.writeObject("Consumer");
                    out.flush();
                    out.writeObject("SEARCH TOPIC");
                    out.flush();
                    out.writeObject(infoconsumer);
                    out.flush();
                    String request = input.getText().toString();
                    out.writeObject(request);
                    if(in.readObject().equals("VIDEO FOUND")){
                        String answer = (String) in.readObject();
                        if (answer.equals("RESPONSIBLE")){
                            while (!((String) in.readObject()).equals("Done")) {
                                int size = (Integer) in.readObject(); //number of chunks
                                ArrayList<byte[]> v = new ArrayList<>();
                                byte[] tempVid;
                                for (int i = 0; i < size; i++) {
                                    tempVid = (byte[]) in.readObject();
                                    v.add(tempVid);
                                }
                                File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + File.separator + "Publish"); //We create a new folder named "Publish"
                                file.mkdir();																							//des katagrafi 4 for saving location
                                File newFile = new File (file.getAbsolutePath() + File.separator + counter + ".mp4");
                                FileOutputStream out1 = new FileOutputStream(newFile);

                                for (byte[] b : v) {
                                    out1.write(b);
                                }
                                out1.close();
                                Log.e("video saved.","OK");
                                Toast toa=Toast.makeText(getApplicationContext(),"VIDEO SAVED",Toast.LENGTH_SHORT);
                                toa.show();
                            }
                        } if (answer.equals("NOT RESPONSIBLE")){
                            String correct_ip = (String) in.readObject();
                            int correct_port = (Integer) in.readObject();
                            in.close();
                            out.close();
                            requestSocket.close();
                            requestSocket = new Socket(correct_ip, correct_port);
                            out = new ObjectOutputStream(requestSocket.getOutputStream());
                            in = new ObjectInputStream(requestSocket.getInputStream());
                            out.writeObject("Consumer");
                            out.flush();
                            out.writeObject("SEARCH TOPIC");
                            out.flush();
                            out.writeObject(infoconsumer);
                            out.flush();
                            out.writeObject(request);
                            out.flush();
                            String found=(String) in.readObject();
                            String answer2 = (String) in.readObject();
                            if(found.equals("VIDEO FOUND")){
                                if (answer2.equals("RESPONSIBLE")) {
                                    while (!((String) in.readObject()).equals("Done")) {
                                        int size = (Integer) in.readObject(); //number of chunks
                                        ArrayList<byte[]> v = new ArrayList<>();
                                        byte[] tempVid;
                                        for (int i = 0; i < size; i++) {
                                            tempVid = (byte[]) in.readObject();
                                            v.add(tempVid);
                                        }
                                        File file = new File(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + File.separator + "Publish"); //We create a new folder named "Publish"
                                        file.mkdir();																							//des katagrafi 4 for saving location
                                        File newFile = new File (file.getAbsolutePath() + File.separator + counter + ".mp4");
                                        FileOutputStream out1 = new FileOutputStream(newFile);

                                        for (byte[] b : v) {
                                            out1.write(b);
                                        }
                                        out1.close();
                                        Log.e("video saved.","OK");
                                        Toast to=Toast.makeText(getApplicationContext(),"VIDEO SAVED",Toast.LENGTH_SHORT);
                                        to.show();
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Log.e("Video","Not Found");
                        Toast t=Toast.makeText(getApplicationContext(),"VIDEO NOT FOUND",Toast.LENGTH_SHORT);
                        t.show();
                    }
                }catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}



