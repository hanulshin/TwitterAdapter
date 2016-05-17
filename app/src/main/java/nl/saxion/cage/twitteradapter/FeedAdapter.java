package nl.saxion.cage.twitteradapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FeedAdapter extends ArrayAdapter<Tweets> {

    private List<Tweets> tweets = new ArrayList<>();

    public FeedAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        tweets=objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }

        Tweets tweet = tweets.get(position);

        TextView textText = (TextView) convertView.findViewById(R.id.TextView2);
        textText.setText(tweet.getText());

        TextView nameText = (TextView)convertView.findViewById(R.id.NameView);
        nameText.setText(tweet.getUser().getName());

        TextView screenNameText=(TextView)convertView.findViewById(R.id.ScreenNameView);
        screenNameText.setText("@"+tweet.getUser().getScreen_name());

        TextView retweetCountText= (TextView)convertView.findViewById(R.id.RetweetView);
        retweetCountText.setText("Retweets: "+String.valueOf(tweet.getRetweet_count()));

        TextView likesText=(TextView)convertView.findViewById(R.id.LikeView2);
        likesText.setText("Likes: "+String.valueOf(tweet.getFavourite_count()));

        TextView dateText=(TextView)convertView.findViewById(R.id.DateView);
        dateText.setText(tweet.getCreated_at());

        return convertView;
    }
}
