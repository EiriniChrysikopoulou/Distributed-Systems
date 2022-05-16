package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;


public class ViewVideosActivity extends AppCompatActivity {

    VideoView videoview;
    private int GALLERY = 1;
    String selectedVideoPath;
    Button ok_button;
    Button btn;

    public void chooseVideoFromGallary() {

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        Uri uri = Uri.parse(getApplicationContext().getExternalFilesDir(null).getAbsolutePath() + File.separator + "Publish");
        galleryIntent.setDataAndType(uri, "*/*");

        startActivityForResult(galleryIntent, GALLERY);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewvideos);
        Intent intent = getIntent();

        btn = (Button) findViewById(R.id.view_videos_btn);
        ok_button = (Button) findViewById(R.id.ok_btn);
        videoview = (VideoView) findViewById(R.id.vid);
        chooseVideoFromGallary();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ok_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

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
                //saveVideoToInternalStorage(selectedVideoPath);
                videoview.setVideoURI(contentURI);
                videoview.requestFocus();
                videoview.start();
            }
        }
    }
}
