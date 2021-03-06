package nl.saxion.cage.twitteradapter.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.saxion.cage.twitteradapter.Actitivies.ZoomActivity;
import nl.saxion.cage.twitteradapter.AsyncTasks.LikeTweetAsync;
import nl.saxion.cage.twitteradapter.AsyncTasks.UnlikeTweetAsync;
import nl.saxion.cage.twitteradapter.Model;
import nl.saxion.cage.twitteradapter.R;
import nl.saxion.cage.twitteradapter.Tweets.Tweets;

public class CardTweetAdapter extends RecyclerView.Adapter<CardTweetAdapter.CardViewHolder> {

    /**
     * list of tweets
     */
    private List<Tweets> tweetsList;

    /**
     * context for loading user media image
     */
    private Context context;

    /**
     * set variables
     *
     * @param tweetsList
     * @param con
     */
    public CardTweetAdapter(List<Tweets> tweetsList, Context con) {
        this.tweetsList = tweetsList;
        this.context = con;
    }

    /**
     * highlight entities, set views, set onclick listeners
     *
     * @param cardViewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int position) {
        //single tweet for getting tweet information
        Tweets tweet = tweetsList.get(position);

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

        //set text in textViews
        cardViewHolder.textText.setText(spanText);
        cardViewHolder.nameText.setText(tweet.getUser().getName());
        cardViewHolder.screenNameText.setText("@" + tweet.getUser().getScreen_name());
        cardViewHolder.dateText.setText(tweet.getCreated_at());
        cardViewHolder.likesText.setText(String.valueOf(tweet.getFavourite_count()));
        cardViewHolder.retweetsCountText.setText(String.valueOf(tweet.getRetweet_count()));

        //load profile image
        Picasso.with(context).load(tweet.getUser().getProfile_image_url()).into(cardViewHolder.profileImage);

        //reset media image (it gets messed up when it isn't reset)
        Bitmap bitmap = BitmapFactory.decodeFile("IMAGE_RESET");
        cardViewHolder.media.setImageBitmap(bitmap);

        //load media image
        if (tweet.getEntities().getMedia(
        ).size() > 0) {
            //url for loading media image
            String mediaUrl = tweet.getEntities().getMedia().get(0).getMedia_url();
            Log.d("mediaUrl", mediaUrl);
            Picasso.with(context)
                    .load(tweet.getEntities().getMedia().get(0).getMedia_url())
                    .resize(1500, 500)
                    .centerCrop()
                    .into(cardViewHolder.media);
        }
    }

    /**
     * highlights a portion of text with color
     *
     * @param color
     * @param size
     * @param indices
     * @param spanText
     * @return the highlighted text
     */
    Spannable setSpan(int color, int size, int[] indices, Spannable spanText) {
        for (int h = 0; h < size; h++) {
            for (int g = 0; g < size; g++) {
                spanText.setSpan(new ForegroundColorSpan(color), indices[0], indices[1], Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanText;
    }

    /**
     * inflates a card
     *
     * @param viewGroup
     * @param position  the index of the card
     * @return
     */
    @Override
    public CardViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int position) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_item, viewGroup, false);
        //get the viewHolder for getting views
        final CardViewHolder holder = new CardViewHolder(itemView);

        //set imageView for setting onclick
        final ImageView mediaImage = (ImageView) itemView.findViewById(R.id.mediaImage);

        //set imageButton for setting onclick
        final ImageButton likeButton = (ImageButton) itemView.findViewById(R.id.like);

        //set onclick listener for image
        mediaImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent zoomIntent = new Intent(context, ZoomActivity.class);
                    zoomIntent.putExtra("imageUrl", tweetsList.get(position).getEntities().getMedia().get(0).getMedia_url());
                    context.startActivity(zoomIntent);
                }
            }

        });

        //set OnClickListener for like ImageButton
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweetsList.get(position).isFavourited() != true) {
                    LikeTweetAsync likeTweetAsync = new LikeTweetAsync();
                    likeTweetAsync.execute(tweetsList.get(position).getId_str());
                    Model model = Model.getInstance();
                    System.out.println("work " + model.getAccessToken());
                    likeButton.setImageResource(R.drawable.ic_favorite_black_24px);
                    tweetsList.get(position).setFavourited(true);
                } else {
                    UnlikeTweetAsync unlikeTweetAsync = new UnlikeTweetAsync();
                    unlikeTweetAsync.execute(tweetsList.get(position).getId_str());
                    Model model = Model.getInstance();
                    likeButton.setImageResource(R.drawable.ic_favorite_border_black_24px);
                    System.out.println("work " + model.getAccessToken());
                    tweetsList.get(position).setFavourited(false);
                }
            }
        });
        return holder;
    }

    /**
     * sets and assigns views values
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        //define views
        private TextView textText;
        private TextView nameText;
        private TextView screenNameText;
        private TextView retweetsCountText;
        private TextView likesText;
        private TextView dateText;
        private ImageView profileImage;
        private ImageView media;
        private View view;
        private ImageButton like;

        //cardViewHolder for accessing views
        public CardViewHolder(View itemView) {
            super(itemView);

            //set views
            textText = (TextView) itemView.findViewById(R.id.TextView);
            nameText = (TextView) itemView.findViewById(R.id.NameView);
            screenNameText = (TextView) itemView.findViewById(R.id.ScreenNameView);
            retweetsCountText = (TextView) itemView.findViewById(R.id.RetweetView);
            likesText = (TextView) itemView.findViewById(R.id.LikeView);
            dateText = (TextView) itemView.findViewById(R.id.DateView);
            profileImage = (ImageView) itemView.findViewById(R.id.profileView);
            media = (ImageView) itemView.findViewById(R.id.mediaImage);
            like = (ImageButton) itemView.findViewById(R.id.like);
        }
    }

    @Override
    public int getItemCount() {
        return tweetsList.size();
    }
}
