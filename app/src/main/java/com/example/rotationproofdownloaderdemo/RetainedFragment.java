package com.example.rotationproofdownloaderdemo;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RetainedFragment extends Fragment {

    DownloadTask downloadTask;
    private Activity activity;

    public RetainedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null; //no ui
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true); //saves the state on configuration changes.
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
        if (downloadTask != null) downloadTask.onActivityAttached(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        downloadTask.onActivityDetached();
    }

    public void startDownload(String url, String filePath) {
        downloadTask = new DownloadTask(activity);
        downloadTask.execute(url, filePath);
    }
}
