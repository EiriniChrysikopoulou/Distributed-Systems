package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

import AppNode.Consumer;
import AppNode.Publisher;
import AppNode.RequestThread;
import Utils.ChannelName;
import Utils.VideoFile;

public class BrokerActivity extends AppCompatActivity {

    Button next;
    EditText brokerPort;
    String UserName;
    String UserIp;
    String UserPort;
    Consumer consumer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker);
        next=(Button)findViewById(R.id.next_1Button);
        brokerPort=(EditText)findViewById(R.id.BrokerPort);

        Intent intent=getIntent();

        UserName=intent.getStringExtra("userName");
        UserIp=intent.getStringExtra("Ip");
        UserPort=intent.getStringExtra("Port");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ChannelName channelName=new ChannelName(UserName);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value= brokerPort.getText().toString();
                int finalValue=Integer.parseInt(value);
                consumer=new Consumer(UserIp,Integer.parseInt(UserPort)+1,channelName);

                try {
                    consumer.init("192.168.56.1", finalValue, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                consumer.consumerPrint();
                Publisher publisher=new Publisher(channelName,UserIp,Integer.parseInt(UserPort));
                try {
                    RequestThread thread =new RequestThread(publisher.getInfo());
                    thread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                publisher.getBrokerList(finalValue);
                try {
                    publisher.sendPublisherInfo();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext() , MenuActivity.class);
                intent.putExtra("publisherInfo",publisher.getInfo());
                intent.putExtra("consumerInfo",consumer.getInfo());
                startActivity(intent);

            }
        });
    }
}