package com.example.rotationproofdownloaderdemo;

import android.app.Activity;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTask extends AsyncTask<String, Integer, Boolean> {

    private int totalContentLength = -1;
    private int totalContentRead = -1;
    private Activity activity;

    public DownloadTask(Activity activity) {
        onActivityAttached(activity);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (activity != null) ((MainActivity) activity).showProgress(true);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String downloadUrl = params[0];
        String filePath = params[1];
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
                int percentageRead = (int) (((double) totalContentRead / totalContentLength) * 100);
                System.out.println("percentageRead = " + percentageRead);
                publishProgress(percentageRead);
            }
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpURLConnection != null) httpURLConnection.disconnect();
                if (inputStream != null) inputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return success;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (activity != null) ((MainActivity) activity).updateProgressbar(values[0]);
        else System.out.println("Activity null; Skipping " + values[0]);
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        System.out.println("success = " + success);
        if (activity != null) {
            ((MainActivity) activity).showProgress(false);
            ((MainActivity) activity).onDownloadFinished(success);
        }
    }

    public void onActivityAttached(Activity activity) {
        this.activity = activity;
    }

    public void onActivityDetached() {
        activity = null;
    }
}