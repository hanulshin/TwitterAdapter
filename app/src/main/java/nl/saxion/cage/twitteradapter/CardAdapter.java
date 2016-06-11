package nl.saxion.cage.twitteradapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import nl.saxion.cage.twitteradapter.Entities.Entities;
import nl.saxion.cage.twitteradapter.Entities.URL;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    Tweets tweet;

    private List<Tweets> tweetsList;
     Context con;
     String  src=null;
    public CardAdapter(Feed feed, int item_tweet, List<Tweets> tweetsList, Context con) {
        this.tweetsList = tweetsList;
        this.con=con;
    }

    @Override
    public int getItemCount() {
        return tweetsList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        //get tweet from position
        tweet = tweetsList.get(i);

        //create spannable for highlighting
        Spannable spanText = Spannable.Factory.getInstance().newSpannable(tweet.getText());

        //set default highlight color
        int highlightColor = Color.parseColor("#2baeff");

        //highlight urls
        for (int j = 0; j < tweet.getEntities().getUrls().size(); j++) {
            setSpan(highlightColor, tweet.getEntities().getUrls().size(), tweet.getEntities().getUrls().get(j).getIndices(), spanText);
        }
        //highlight hashtags
        for (int j = 0; j < tweet.getEntities().getHashtags().size(); j++) {
            setSpan(highlightColor, tweet.getEntities().getHashtags().size(), tweet.getEntities().getHashtags().get(j).getIndices(), spanText);
        }
        //highlight user mentions
        for (int j = 0; j < tweet.getEntities().getUser_mentions().size(); j++) {
            setSpan(highlightColor, tweet.getEntities().getUser_mentions().size(), tweet.getEntities().getUser_mentions().get(j).getIndices(), spanText);
        }


//        for (int h = 0; h < tweet.getEntities().getUrls().size(); h++) {
//            for (int g = 0; g < tweet.getEntities().getUrls().size(); g++) {
//                spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#2baeff")), tweet.getEntities().getUrls().get(g).getIndices()[0],
//                        tweet.getEntities().getUrls().get(g).getIndices()[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//
//        for (int j = 0; j < tweet.getEntities().getHashtags().size(); j++) {
//            for (int k = 0; k < tweet.getEntities().getHashtags().size(); k++) {
//                spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#2baeff")), tweet.getEntities().getHashtags().get(k).getIndices()[0],
//                        tweet.getEntities().getHashtags().get(k).getIndices()[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }
//
//        for (int j = 0; j < tweet.getEntities().getUser_mentions().size(); j++) {
//            for (int k = 0; k < tweet.getEntities().getUser_mentions().size(); k++) {
//                spanText.setSpan(new ForegroundColorSpan(Color.parseColor("#2baeff")), tweet.getEntities().getUser_mentions().get(k).getIndices()[0],
//                        tweet.getEntities().getUser_mentions().get(k).getIndices()[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }

        cardViewHolder.textText.setText(spanText);

        cardViewHolder.nameText.setText(tweet.getUser().getName());
        cardViewHolder.screenNameText.setText("@"+tweet.getUser().getScreen_name());
        cardViewHolder.dateText.setText(tweet.getCreated_at());
        cardViewHolder.likesText.setText("Likes: " + String.valueOf(tweet.getFavourite_count()));

        Picasso.with(con).load(tweet.getUser().getProfile_image_url()).into(cardViewHolder.profileImage);

    }

    Spannable setSpan(int color, int size, int[] indices, Spannable spanText) {

        for (int h = 0; h < size; h++) {
            for (int g = 0; g < size; g++) {
                spanText.setSpan(new ForegroundColorSpan(color), indices[0], indices[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanText;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_item_alt, viewGroup, false);

        return new CardViewHolder(itemView);
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        protected TextView textText;
        protected TextView nameText;
        protected TextView screenNameText;
        protected TextView retweetsCountText;
        protected TextView likesText;
        protected TextView dateText;
        protected ImageView profileImage;

        public CardViewHolder(View convertView) {
            super(convertView);

            textText = (TextView) convertView.findViewById(R.id.TextView);
            nameText = (TextView) convertView.findViewById(R.id.NameView);
            screenNameText = (TextView) convertView.findViewById(R.id.ScreenNameView);
            retweetsCountText = (TextView) convertView.findViewById(R.id.RetweetView);
            likesText = (TextView) convertView.findViewById(R.id.LikeView);
            dateText = (TextView) convertView.findViewById(R.id.DateView);
            profileImage = (ImageView) convertView.findViewById(R.id.imageView);
        }
    }
}
