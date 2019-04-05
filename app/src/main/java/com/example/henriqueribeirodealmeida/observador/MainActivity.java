package com.example.henriqueribeirodealmeida.observador;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidhiddencamera.HiddenCameraFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getName();

    private HiddenCameraFragment mHiddenCameraFragment;
    private ImageView pictureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pictureView = findViewById(R.id.picture);

        Button takePictureButton = findViewById(R.id.take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHiddenCameraFragment != null) {    //Remove fragment from container if present
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(mHiddenCameraFragment)
                            .commit();
                    mHiddenCameraFragment = null;
                }

                startService(new Intent(MainActivity.this, CamService.class));
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("sendPictureToActivity"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getBundleExtra("bundle");
            Bitmap image = bundle.getParcelable("picture");

            new postImageAsyncTask().execute(image);
        }
    };

    private class postImageAsyncTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... image) {
            return QueryUtils.postImage(image[0]);
        }
    }
}
