package nl.saxion.cage.twitteradapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Cage on 5/19/16.
 */
public abstract class LoadProfileAsync extends AsyncTask< String, Double, byte[]> {

    static Bitmap bitmap;
    protected static Bitmap doInBackground(String src) {
        try {
            URL url = new URL(src);
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bmp;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
    }
}
