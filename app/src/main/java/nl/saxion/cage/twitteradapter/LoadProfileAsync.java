package nl.saxion.cage.twitteradapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Cage on 5/19/16.
 */
public class LoadProfileAsync extends AsyncTask< ImageView, Void, Bitmap> {

    static Bitmap bitmap;
    ImageView imageView = null;

    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        this.imageView = imageViews[0];
return null;
        //return download_image((String)imageView.getTag());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

    }

    private Bitmap download_image(String src) {
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
}
