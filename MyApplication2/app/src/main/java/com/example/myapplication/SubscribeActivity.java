package com.example.myapplication;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import Info.InfoBroker;
import Info.InfoConsumer;
import Info.InfoPublisher;

public class SubscribeActivity extends AppCompatActivity {

    Button sub;
    EditText topic;
    TextView message;
    InfoConsumer infoC;
    InfoPublisher infoP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        sub = (Button) findViewById(R.id.Sub);
        topic = (EditText) findViewById(R.id.topic);
        message = (TextView) findViewById(R.id.Sub_view);
        Intent intent = getIntent();
        infoC = (InfoConsumer) intent.getSerializableExtra("consumerInfo");
        infoP = (InfoPublisher) intent.getSerializableExtra("publisherInfo");
    }


    @Override
    protected void onStart() {
        super.onStart();
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_topic = topic.getText().toString();
                infoC.addTopic(new_topic);
                for (InfoBroker b : infoC.getBrokers()) {
                    Socket request = null;
                    try {
                        request = new Socket(b.ip, b.port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ObjectOutputStream out = null;
                    try {
                        out = new ObjectOutputStream(request.getOutputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ObjectInputStream in = null;
                    try {
                        in = new ObjectInputStream(request.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    AppNode.UpdateBrokerSub t = new AppNode.UpdateBrokerSub(request, in, out, infoC);
                    t.start();
                }

                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("consumerInfo", infoC);
                intent.putExtra("publisherInfo", infoP);
                startActivity(intent);

            }
        });
    }
}