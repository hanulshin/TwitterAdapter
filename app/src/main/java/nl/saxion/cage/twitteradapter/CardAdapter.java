package nl.saxion.cage.twitteradapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hanulshin on 2016-05-16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    Tweets tweet;

    //private List<ContactInfo> contactList;
    private List<Tweets> tweetsList;

    public CardAdapter(Feed feed, int item_tweet, List<Tweets> tweetsList) {
        this.tweetsList = tweetsList;
    }

    @Override
    public int getItemCount() {
        return tweetsList.size();
    }

    @Override
    public void onBindViewHolder(CardViewHolder cardViewHolder, int i) {
        tweet = tweetsList.get(i);
        cardViewHolder.textText.setText(tweet.getText());
        cardViewHolder.nameText.setText(tweet.getUser().getName());
        cardViewHolder.screenNameText.setText(tweet.getUser().getScreen_name());
        cardViewHolder.dateText.setText(tweet.getCreated_at());
        cardViewHolder.likesText.setText("Likes: "+String.valueOf(tweet.getFavourite_count()));
        cardViewHolder.retweetCountText.setText("Retweet: "+String.valueOf(tweet.getRetweet_count()));
///
//        ContactInfo ci = contactList.get(i);
//        contactViewHolder.vName.setText(ci.name);
//        contactViewHolder.vSurname.setText(ci.surname);
//        contactViewHolder.vEmail.setText(ci.email);
//        contactViewHolder.vTitle.setText(ci.name + " " + ci.surname);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_item, viewGroup, false);

        return new CardViewHolder(itemView);
    }


    public static class CardViewHolder extends RecyclerView.ViewHolder{

        protected TextView textText;
        protected TextView nameText;
        protected TextView screenNameText;
        protected TextView retweetCountText;
        protected TextView likesText;
        protected TextView dateText;

        public CardViewHolder(View convertView) {
            super(convertView);

            textText = (TextView) convertView.findViewById(R.id.TextView);
            nameText = (TextView)convertView.findViewById(R.id.NameView);
            screenNameText = (TextView)convertView.findViewById(R.id.ScreenNameView);
            retweetCountText = (TextView)convertView.findViewById(R.id.RetweetView);
            likesText = (TextView) convertView.findViewById(R.id.LikeView);
            dateText = (TextView)convertView.findViewById(R.id.DateView);
        }

        public void setTextText(TextView textText) {
            this.textText = textText;
        }

        public void setNameText(TextView nameText) {
            this.nameText = nameText;
        }

        public void setScreenNameText(TextView screenNameText) {
            this.screenNameText = screenNameText;
        }

        public void setRetweetCountText(TextView retweetCountText) {
            this.retweetCountText = retweetCountText;
        }

        public void setLikesText(TextView likesText) {
            this.likesText = likesText;
        }

        public void setDateText(TextView dateText) {
            this.dateText = dateText;
        }
    }

}
