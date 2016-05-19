package nl.saxion.cage.twitteradapter;

import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    Tweets tweet;

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

        String tweetText = tweet.getText().replace("\n"," ");

        cardViewHolder.textText.setText(tweet.getText());
        for (int j = 0; j < tweet.getEntities().getHashtags().size(); j++) {
            Spannable spanText = Spannable.Factory.getInstance().newSpannable(tweetText);
            for (int k = 0; k < tweet.getEntities().getHashtags().size(); k++) {
                spanText.setSpan(new UnderlineSpan(), tweet.getEntities().getHashtags().get(k).getIndices()[0],
                        tweet.getEntities().getHashtags().get(k).getIndices()[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            cardViewHolder.textText.setText(spanText);
        }

        cardViewHolder.nameText.setText(tweet.getUser().getName());
        cardViewHolder.screenNameText.setText("@"+tweet.getUser().getScreen_name());
        cardViewHolder.dateText.setText(tweet.getCreated_at());
        cardViewHolder.likesText.setText("Likes: " + String.valueOf(tweet.getFavourite_count()));
        cardViewHolder.retweetsCountText.setText("Retweet: " + String.valueOf(tweet.getRetweet_count()));
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

        public CardViewHolder(View convertView) {
            super(convertView);

            textText = (TextView) convertView.findViewById(R.id.TextView);
            nameText = (TextView) convertView.findViewById(R.id.NameView);
            screenNameText = (TextView) convertView.findViewById(R.id.ScreenNameView);
            retweetsCountText = (TextView) convertView.findViewById(R.id.RetweetView);
            likesText = (TextView) convertView.findViewById(R.id.LikeView);
            dateText = (TextView) convertView.findViewById(R.id.DateView);
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

        public void setRetweetsCountText(TextView retweetsCountText) {
            this.retweetsCountText = retweetsCountText;
        }

        public void setLikesText(TextView likesText) {
            this.likesText = likesText;
        }

        public void setDateText(TextView dateText) {
            this.dateText = dateText;
        }
    }
}
