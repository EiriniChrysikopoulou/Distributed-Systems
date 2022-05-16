package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.VideoView;
import Utils.*;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import AppNode.NotifyBrokerSub;
import AppNode.UpdateBrokers;
import Info.InfoBroker;
import Info.InfoConsumer;
import Info.InfoPublisher;
import Utils.Utils;
import Utils.VideoFile;

public class UploadActivity extends AppCompatActivity {

    private VideoView videoView;
    private int GALLERY = 1, CAMERA = 2;
    EditText hashtags;
    InfoPublisher infoP;
    Button ok_button;
    PublisherPair video;
    Button btn;
    String selectedVideoPath;
    String[] str;
    InfoConsumer infoC;
    VideoFile v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        Intent intent = getIntent();
        infoP = (InfoPublisher) intent.getSerializableExtra("publisherInfo");
        infoC = (InfoConsumer) intent.getSerializableExtra("consumerInfo");
        for(InfoBroker b: infoP.getBrokers()){
            Log.e("BrokerPort", String.valueOf(b.port));
        }
        btn=(Button) findViewById(R.id.btn);
        ok_button = (Button) findViewById(R.id.ok_btn);
        videoView = (VideoView) findViewById(R.id.vv);
        hashtags = (EditText) findViewById(R.id.inputHashtags);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ok_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                str = hashtags.getText().toString().split(" ");
                video = new PublisherPair(str, selectedVideoPath);
                infoP.addPair(video);
                try {
                    v = new VideoFile(selectedVideoPath, str);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                List<String> temp_array = Arrays.asList(str);
                for (String s : temp_array) {
                    infoP.addTopic(s);
                    try {
                        hashTopic(s);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                for(InfoBroker b :infoP.getBrokers()){
                    for (String t : b.topics){
                        Log.e("Broker-Topic",b.getPort()+" "+t);
                    }
                }
                try {
                    notifyBrokersForHashTags();
                    notifyBroker(v);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("publisherInfo", infoP);
                intent.putExtra("consumerInfo", infoC);
                startActivity(intent);
            }
        });
    }

    public void notifyBroker(VideoFile v) throws IOException {
        boolean flag = false;
        int i = 0;
        while (!flag && i < infoP.getBrokers().size()) { //send subscription video to random broker with at least one of video's hashtags
            for (String x : v.associatedHashtags) {
                if (infoP.getBrokers().get(i).topics.contains(x)) {
                    flag = true;
                }
            }
            if (flag) {
                Socket request = new Socket(infoP.getBrokers().get(i).ip, infoP.getBrokers().get(i).port);
                ObjectOutputStream out = new ObjectOutputStream(request.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(request.getInputStream());
                Thread t = new Thread(new NotifyBrokerSub(request, in, out,v, infoP));
                t.start();

            }
            i++;
        }
    }

    public void notifyBrokersForHashTags() throws IOException {
        for (InfoBroker b : infoP.getBrokers()) {
            Socket request = new Socket(b.ip, b.port);
            Log.e("Notifying", String.valueOf(b.getPort()));
            ObjectOutputStream out = new ObjectOutputStream(request.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(request.getInputStream());
            UpdateBrokers t = new UpdateBrokers(request, in, out, infoP.getBrokers(),infoP);
            t.start();

        }
    }


    public void hashTopic(String x) throws IOException {
        BigInteger hx = Utils.MD5(x);
        Collections.sort(infoP.getBrokers());
        if (hx.compareTo(infoP.getBrokers().get(infoP.getBrokers().size() - 1).maxHash) > 0) {
            hx = Utils.MD5(x).mod(infoP.getBrokers().get(infoP.getBrokers().size() - 1).maxHash);
        }
        int j = 0;
        while (j < infoP.getBrokers().size()) {
            if (hx.compareTo(infoP.getBrokers().get(j).maxHash) <= 0) {
                infoP.getBrokers().get(j).topics.add(x);
                break;
            } else {
                j++;
            }
        }
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select video from gallery",
                "Record video from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                chooseVideoFromGallary();
                                break;
                            case 1:
                                takeVideoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void chooseVideoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("result", "" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {

            return;
        }
        if (requestCode == GALLERY) {

            if (data != null) {
                Uri contentURI = data.getData();

                selectedVideoPath = getPath(contentURI);

                Log.e("selectedVideoPath", selectedVideoPath);
                videoView.setVideoURI(contentURI);
                videoView.requestFocus();
                videoView.start();
            }

        } else if (requestCode == CAMERA) {
            Uri contentURI = data.getData();
            String recordedVideoPath = getPath(contentURI);
            Log.d("recordedVideoPath", recordedVideoPath);
            saveVideoToInternalStorage(recordedVideoPath);
            videoView.setVideoURI(contentURI);
            videoView.requestFocus();
            videoView.start();
        }
    }

    private void saveVideoToInternalStorage(String filePath) {

        File newfile;

        try {

            File currentFile = new File(filePath);
            File wallpaperDirectory = new File(String.valueOf(Environment.getExternalStorageDirectory()));
            newfile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");

            if (!wallpaperDirectory.exists()) {
                wallpaperDirectory.mkdirs();
            }

            if (currentFile.exists()) {

                InputStream in = new FileInputStream(currentFile);
                OutputStream out = new FileOutputStream(newfile);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.e("saved successfully", "Video file saved successfully.");
            } else {
                Log.e("saving failed", "Video saving failed. Source file missing.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}

