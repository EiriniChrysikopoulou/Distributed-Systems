package com.example.myapplication;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ViewTopicsActivity extends AppCompatActivity {

    TextView DisplayStringArray;
    LinearLayout LinearLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayoutView = new LinearLayout(this);
        DisplayStringArray = new TextView(this);
        DisplayStringArray.setTextSize(25);
        LinearLayoutView.addView(DisplayStringArray);

    }

    @Override
    protected void onStart() {
        super.onStart();
        DisplayStringArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        Socket request = null;
        try {
            request = new Socket("192.168.56.1", 5000);
            ObjectInputStream in = new ObjectInputStream(request.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(request.getOutputStream());
            out.writeObject("Consumer");
            out.flush();
            out.writeObject("Get topics");
            out.flush();
            ArrayList<String> topics = new ArrayList<>();
            topics = (ArrayList<String>) in.readObject();
            for (int i=0; i<topics.size();i++){

                DisplayStringArray.append(topics.get(i));
                DisplayStringArray.append("\n"); }

            setContentView(LinearLayoutView);
            for (String s : topics) {
                Log.e("Topic", s);
            }

        } catch (UnknownHostException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
