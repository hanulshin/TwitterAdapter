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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardUserAdapter extends RecyclerView.Adapter<CardUserAdapter.CardViewHolder> {
    Users users;

    private List<Users> usersList;

    Context context;

    public CardUserAdapter(List<Users> usersList, Context con) {
        this.usersList = usersList;
        this.context = con;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        //define views
        protected TextView nameText;
        protected TextView screenNameText;
        protected ImageView profileImage;

        //cardViewHolder for accessing views
        public CardViewHolder(View convertView) {
            super(convertView);

            //set views
            nameText = (TextView) convertView.findViewById(R.id.friend_name);
            screenNameText = (TextView) convertView.findViewById(R.id.friend_screenname);
            profileImage = (ImageView) convertView.findViewById(R.id.friend_profile);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int i) {
        //get tweet from position
        users = usersList.get(i);

        //set text in textViews
        cardViewHolder.nameText.setText(users.getName());
        cardViewHolder.screenNameText.setText("@" + users.getScreen_name());

        //load profile image
        Picasso.with(context).load(users.getProfile_image_url()).into(cardViewHolder.profileImage);
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.friend_card, viewGroup, false);
        return new CardViewHolder(itemView);
    }
}
