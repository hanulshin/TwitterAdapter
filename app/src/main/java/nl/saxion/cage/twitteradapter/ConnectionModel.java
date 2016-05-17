package nl.saxion.cage.twitteradapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Cage on 5/17/16.
 */
public class ConnectionModel {
    ConnectionModel(){
        URL url = null;
        try {
            url = new URL("http://<url>");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setDoInput(true);
        int response = 0;

        try {
            response = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == 200) {
            //everything is okay :D
        } else {
            //we fucked up
        }

        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        //tells client we want to get input from it
        conn.setDoInput(true);

        try {
            InputStream is = conn.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
