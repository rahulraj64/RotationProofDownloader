package com.example.rotationproofdownloaderdemo;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText etUrl;
    Button btnDownload;
    ProgressBar progressBar;
    ImageView imageView;

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
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).
                        getAbsolutePath() + "/" + Uri.parse(url).getLastPathSegment();
                if (!TextUtils.isEmpty(url)) {
                    new DownloadTask(filePath).execute(url);
                }
            }
        });
    }

    class DownloadTask extends AsyncTask<String, Integer, Boolean> {

        private int totalContentLength = -1;
        private int totalContentRead = -1;
        private String filePath;

        public DownloadTask(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String downloadUrl = params[0];
            boolean success = false;
            URL url;
            HttpURLConnection httpURLConnection = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;
            File file;
            try {
                url = new URL(downloadUrl);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                totalContentLength = httpURLConnection.getContentLength();
                System.out.println("totalContentLength = " + totalContentLength);
                inputStream = httpURLConnection.getInputStream();
                file = new File(filePath);
                fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[512];
                int read = -1;
                while ((read = inputStream.read(buffer)) != -1) {
                    System.out.println("DownloadTask.doInBackground Read " + read);
                    fileOutputStream.write(buffer, 0, read);
                    totalContentRead += read;
                    int percentageRead = (int) (((double)totalContentRead / totalContentLength) * 100);
                    System.out.println("percentageRead = " + percentageRead);
                    publishProgress(percentageRead);
                }
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(httpURLConnection != null) httpURLConnection.disconnect();
                    if(inputStream != null) inputStream.close();
                    if(fileOutputStream != null) fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return success;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            System.out.println("success = " + success);
            progressBar.setVisibility(View.INVISIBLE);
            if(success) imageView.setImageURI(Uri.fromFile(new File(filePath)));
            else Toast.makeText(MainActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }
    }
}