package nl.saxion.cage.twitteradapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ZoomActivity extends AppCompatActivity {

    //imageView to hold image
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_zoom);

        //set imageView
        imageView = (ImageView) findViewById(R.id.image_zoom) ;

        //set imageView onclicklistener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get intent extras
        Intent intent = getIntent();
        String url = intent.getExtras().getString("imageUrl");

        //load image
        Picasso.with(this).load(url).into(imageView);
    }
}
