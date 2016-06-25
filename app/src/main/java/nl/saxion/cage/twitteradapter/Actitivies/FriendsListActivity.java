package nl.saxion.cage.twitteradapter.Actitivies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.github.scribejava.core.model.OAuth1AccessToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import nl.saxion.cage.twitteradapter.AsyncTasks.GetFriendsListAsync;
import nl.saxion.cage.twitteradapter.Adapters.CardUserAdapter;
import nl.saxion.cage.twitteradapter.Model;
import nl.saxion.cage.twitteradapter.R;
import nl.saxion.cage.twitteradapter.Users;

public class FriendsListActivity extends AppCompatActivity {

    /**
     * list of users
     */
    List<Users> users = new ArrayList<>();

    /**
     * String json file containing list of friends
     */
    private String jsonFriendList;

    /**
     * Card adapter for users
     */
    private CardUserAdapter adapter;

    /**
     * accessToken for signing requests
     */
    private OAuth1AccessToken accessToken;

    /**
     * set user cardView adapter, set accessToken from model,
     * load friend list
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_list);

        //card view adapter
        adapter = new CardUserAdapter(users, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardListFriend);
        recyclerView.setHasFixedSize(true);

        //linear layout manager used for recyclerView (cardView)
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        //set recyclerView layout manager & adapter
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

        //get Model instance
        Model model = Model.getInstance();

        //set accessToken
        accessToken = model.getAccessToken();

        //get list of friends
        getFriendsList();
    }

    /**
     * Runs a getFriendsList async task and updates the cardView
     */
    private void getFriendsList() {
        if (accessToken != null) {
            GetFriendsListAsync getFriendsList = new GetFriendsListAsync();
            getFriendsList.execute(accessToken.getToken(), accessToken.getTokenSecret());
            try {
                jsonFriendList = getFriendsList.get();
                updateCardView();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads json file, and updates cardView
     * The time it takes is also logged
     */
    private void updateCardView() {
        if (jsonFriendList != null) {
            //get start time
            long startTime = System.currentTimeMillis();

            //read objects
            try {
                readJsonToObjects(jsonFriendList);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //calculate and print total time
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            Log.d("object loading time", Long.toString(totalTime) + "ms");

            //update the card view
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Parses JSON file into user objects and adds them to list of users
     *
     * @param file name of the JSON file to be parsed.
     */
    private void readJsonToObjects(String file) throws IOException, JSONException {
        //clear the current list of tweets
        users.clear();

        //create new jObject for reading the file
        JSONObject jObject = new JSONObject(file);

        //get users array
        JSONArray jTweetArray = jObject.getJSONArray("users");

        //loop through users
        for (int i = 0; i < jTweetArray.length(); i++) {
            JSONObject jUserObject = jTweetArray.getJSONObject(i);

            //get user data
            String name = jUserObject.getString("name");
            String screen_name = jUserObject.getString("screen_name");
            String profile_image_url = jUserObject.getString("profile_image_url");

            //create new user object with extracted json data
            Users user = new Users(screen_name, name, profile_image_url);

            //add users to list
            users.add(user);
        }
    }

}
