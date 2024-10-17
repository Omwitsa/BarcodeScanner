package com.wilson.dispatchintakescanner.Utilities;

import android.content.Context;
import android.os.AsyncTask;

public class PostScannedData extends AsyncTask<String, Void, Boolean> {
    private Context context;

    public PostScannedData(Context context) {
        this.context = context;
    }
    @Override
    protected Boolean doInBackground(String... strings) {
        return null;
    }
}