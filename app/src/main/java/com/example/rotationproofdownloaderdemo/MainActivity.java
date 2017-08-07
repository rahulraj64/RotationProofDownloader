package com.example.rotationproofdownloaderdemo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    EditText etUrl;
    Button btnDownload;
    ProgressBar progressBar;
    ImageView imageView;

    RetainedFragment retainedFragment;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUrl = (EditText) findViewById(R.id.url);
        btnDownload = (Button) findViewById(R.id.download);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageView = (ImageView) findViewById(R.id.imageView);

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etUrl.getText().toString();
                filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                        getAbsolutePath() + "/" + Uri.parse(url).getLastPathSegment();
                if (!TextUtils.isEmpty(url)) {
                    retainedFragment.startDownload(url, filePath);
                }
            }
        });

        if (savedInstanceState == null) {
            //Creates the fragment if activity is newly created.
            retainedFragment = new RetainedFragment();
            getSupportFragmentManager().beginTransaction().add(retainedFragment, "RetainedFragment").commit();
        } else {
            //Get the fragment if activity is recreated after a confg change.
            retainedFragment = (RetainedFragment) getSupportFragmentManager().findFragmentByTag("RetainedFragment");
            if (retainedFragment.downloadTask.getStatus() == AsyncTask.Status.RUNNING)
                showProgress(true); //bcoz initial visibility of progress is GONE
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("filePath", filePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        filePath = savedInstanceState.getString("filePath");
    }

    public void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    public void updateProgressbar(int progress) {
        progressBar.setProgress(progress);
    }

    public void onDownloadFinished(boolean success) {
        if (success) imageView.setImageURI(Uri.fromFile(new File(filePath)));
        else Toast.makeText(MainActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
    }
}