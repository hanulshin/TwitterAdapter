package nl.saxion.cage.twitteradapter;

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

import java.util.List;

public class CardTweetAdapter extends RecyclerView.Adapter<CardTweetAdapter.CardViewHolder> {
    Tweets tweet;

    String url;

    boolean likeState = false;

    private List<Tweets> tweetsList;

    Context context;

    public CardTweetAdapter(List<Tweets> tweetsList, Context con) {
        this.tweetsList = tweetsList;
        this.context = con;
    }

    @Override
    public int getItemCount() {
        return tweetsList.size();
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int i) {
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

        //boolean likeState=false;

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
            url = tweet.getEntities().getMedia().get(0).getMedia_url();
            Log.d("url", url);
            Picasso.with(context)
                    .load(tweet.getEntities().getMedia().get(0).getMedia_url())
                    .resize(1500, 500)
                    .centerCrop()
                    .into(cardViewHolder.media);
        }

        //set OnClickListener for images
        cardViewHolder.media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ZoomActivity.class);
                intent.putExtra("imageUrl", url);
                context.startActivity(intent);
            }
        });

        //set OnClickListener for like ImageButton
        cardViewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(likeState!=true){
                    tweet.getId_str();
                    //UnlikeTweetAsync unlikeTweetAsync = new UnlikeTweetAsync();
                    LikeTweetAsync likeTweetAsync = new LikeTweetAsync();
                    likeTweetAsync.execute(tweet.getId_str());
                    Model model = Model.getInstance();
                    System.out.println("work "+model.getAccessToken());
                    cardViewHolder.like.setImageResource(R.drawable.ic_favorite_black_24px);
                    likeState=true;
                }
                else{
                    tweet.getId_str();
                    UnlikeTweetAsync unlikeTweetAsync = new UnlikeTweetAsync();
                    // LikeTweetAsync likeTweetAsync = new LikeTweetAsync();
                    unlikeTweetAsync.execute(tweet.getId_str());
                    Model model = Model.getInstance();
                    cardViewHolder.like.setImageResource(R.drawable.ic_favorite_border_black_24px);
                    System.out.println("work "+model.getAccessToken());
                    likeState=false;
                }



            }
        });
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

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_item, viewGroup, false);
        return new CardViewHolder(itemView);
    }

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
        public CardViewHolder(View convertView) {
            super(convertView);

            //set views
            textText = (TextView) convertView.findViewById(R.id.TextView);
            nameText = (TextView) convertView.findViewById(R.id.NameView);
            screenNameText = (TextView) convertView.findViewById(R.id.ScreenNameView);
            retweetsCountText = (TextView) convertView.findViewById(R.id.RetweetView);
            likesText = (TextView) convertView.findViewById(R.id.LikeView);
            dateText = (TextView) convertView.findViewById(R.id.DateView);
            profileImage = (ImageView) convertView.findViewById(R.id.profileView);
            media = (ImageView) convertView.findViewById(R.id.mediaImage);
            like = (ImageButton) convertView.findViewById(R.id.like);
        }
    }
}
