package com.example.henriqueribeirodealmeida.observador;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidhiddencamera.HiddenCameraFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getName();
    private final static String SUCCESS = "success";

    private TextView timeView;
    private TextView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeView = findViewById(R.id.time);
        errorView = findViewById(R.id.error);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("sendPictureToActivity"));

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {
                        if (QueryUtils.isConnected(getApplication())){
                            startService(new Intent(MainActivity.this, CamService.class));
                        } else {
                            Toast.makeText(MainActivity.this, "Não há conexão com a internet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 0, 1, TimeUnit.MINUTES);
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Date currentTime = Calendar.getInstance().getTime();

            if (s.equals(SUCCESS)){
                timeView.setText(String.valueOf(currentTime.toString()));
                errorView.setVisibility(View.GONE);
            } else {
                String errorOutput = "Failed at " + currentTime.toString();
                errorView.setVisibility(View.VISIBLE);
                errorView.setText(errorOutput);
            }
        }
    }
}
