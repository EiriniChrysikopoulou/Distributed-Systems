package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import AppNode.RequestThread;
import Info.*;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.xml.sax.SAXException;

import java.io.IOException;

import Info.InfoPublisher;

public class MenuActivity extends AppCompatActivity {
    Button Upload;
    Button Search;

    Button Sub;
    Button View_videos;
    Button View_topics;

    InfoPublisher infoP;
    InfoConsumer infoC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Upload=(Button)findViewById(R.id.upload_btn);
        Search=(Button)findViewById(R.id.search_btn);

        Sub=(Button)findViewById(R.id.sub_btn);
        View_videos=(Button)findViewById(R.id.view_videos_btn);
        View_topics=(Button)findViewById(R.id.view_topics_btn);

        Intent intent=getIntent();

        infoP = (InfoPublisher) intent.getSerializableExtra("publisherInfo");
        infoC = (InfoConsumer) intent.getSerializableExtra("consumerInfo");
        Log.e("Publisher OK", String.valueOf(infoP.getPort()));
        Log.e("Consumer OK", String.valueOf(infoC.getPort()));

    }


    @Override
    protected void onStart() {
        super.onStart();
        Upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , UploadActivity.class);
                intent.putExtra("publisherInfo",infoP);
                intent.putExtra("consumerInfo",infoC);
                startActivity(intent);
            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , SearchActivity.class);
                intent.putExtra("consumerInfo",infoC);
                startActivity(intent);
            }
        });
        Sub.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , SubscribeActivity.class);
                intent.putExtra("publisherInfo",infoP);
                intent.putExtra("consumerInfo",infoC);
                startActivity(intent);
            }
        });
       View_videos.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , ViewVideosActivity.class);
                startActivity(intent);
            }
        });
        View_topics.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , ViewTopicsActivity.class);
                startActivity(intent);
            }
        });


    }
}