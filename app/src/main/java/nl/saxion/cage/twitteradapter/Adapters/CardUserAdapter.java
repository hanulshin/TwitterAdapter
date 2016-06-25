package nl.saxion.cage.twitteradapter.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nl.saxion.cage.twitteradapter.R;
import nl.saxion.cage.twitteradapter.Users;

public class CardUserAdapter extends RecyclerView.Adapter<CardUserAdapter.CardViewHolder> {

    /**
     * single user for getting user information
     */
    Users user;

    /**
     * list of users
     */
    private List<Users> users;

    /**
     * context for loading user profile image
     */
    Context context;

    /**
     * set variables
     * @param users list of users
     * @param context context of main class using the adapter
     */
    public CardUserAdapter(List<Users> users, Context context) {
        this.users = users;
        this.context = context;
    }

    /**
     * define and set views
     */
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

    /**
     * set user information
     * @param cardViewHolder
     * @param i index of the card
     */
    @Override
    public void onBindViewHolder(final CardViewHolder cardViewHolder, final int i) {
        //get tweet from position
        user = users.get(i);

        //set text in textViews
        cardViewHolder.nameText.setText(user.getName());
        cardViewHolder.screenNameText.setText("@" + user.getScreen_name());

        //load profile image
        Picasso.with(context).load(user.getProfile_image_url()).into(cardViewHolder.profileImage);
    }

    /**
     * inflate card
     * @param viewGroup
     * @param i
     * @return
     */
    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.friend_card, viewGroup, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
