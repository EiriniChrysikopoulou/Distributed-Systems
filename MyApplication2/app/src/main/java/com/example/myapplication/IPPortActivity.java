package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class IPPortActivity extends AppCompatActivity {
    Button next;
    EditText userIp;
    EditText userPort;
    String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipport);
        next=(Button)findViewById(R.id.nextButton);
        userIp=(EditText)findViewById(R.id.inputIp);
        userPort=(EditText)findViewById(R.id.inputPort);
        Intent intent=getIntent();
        UserName=intent.getStringExtra("userName");

        Log.e("UserName:",UserName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , BrokerActivity.class);
                intent.putExtra("userName",UserName);
                intent.putExtra("Ip",userIp.getText().toString());
                intent.putExtra("Port",userPort.getText().toString());


                startActivity(intent);
            }
        });

    }
}