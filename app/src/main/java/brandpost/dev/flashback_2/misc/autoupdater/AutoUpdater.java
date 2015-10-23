package brandpost.dev.flashback_2.misc.autoupdater;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Viktor on 2015-10-05.
 */
public class AutoUpdater {

    private class VersionCheckTask extends AsyncTask<String, String, Boolean> {

        public VersionCheckTask() {
        }

        @Override
        protected Boolean doInBackground(String... params) {
            HttpsURLConnection connection = null;
            URL url = null;
            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if(url != null) {
                try {
                    connection = (HttpsURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(connection != null) {
                    InputStream inputStream = null;
                    try {
                        inputStream = connection.getInputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(inputStream != null) {
                        Reader reader = null;
                        try {
                             reader = new InputStreamReader(inputStream, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        UpdateResponse response = new Gson().fromJson(reader, UpdateResponse.class);
                        System.out.println(response.toString());
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean updateAvailable) {
            mUpdateAvailable = updateAvailable;
            if(updateAvailable) {
                // Show dialog
            }
        }
    }

    VersionCheckTask mDownloadTask;
    Context mContext;

    public AutoUpdater(Context context) {
        mContext = context;
    }

    boolean mUpdateAvailable = false;

    public void checkForUpdate(String url) {
        mDownloadTask = new VersionCheckTask();
        if(url != null)
            mDownloadTask.execute(url);
    }

    private void applyUpdate() {
        if(!mUpdateAvailable) return;

    }

}
