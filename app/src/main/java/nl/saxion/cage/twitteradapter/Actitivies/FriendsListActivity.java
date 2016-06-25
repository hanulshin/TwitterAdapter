package nl.saxion.cage.twitteradapter.Actitivies;

//imports

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;

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

    //list of tweets
    List<Users> users = new ArrayList<>();
    private EditText editSearch;
    private String bearerToken;
    private String searchJSON;
    private CardUserAdapter adapter;

    //access and bearer token
    private OAuth1AccessToken accessToken;

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

        Model model = Model.getInstance();
        accessToken = model.getAccessToken();

        getFriendsList();
    }

    private void getFriendsList() {
        System.out.println("getting friend list...");
        if (accessToken != null) {
            GetFriendsListAsync getFriendsList = new GetFriendsListAsync();
            getFriendsList.execute(accessToken.getToken(), accessToken.getTokenSecret());
            try {

                //update tweet list and cardView
                searchJSON = getFriendsList.get();

                System.out.println("searchJson: " + searchJSON);

                updateCardView();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        else System.out.println("accesstoken null");
    }

    private void updateCardView() {
        if (searchJSON != null) {
            //get start time
            long startTime = System.currentTimeMillis();

            //read objects
            try {
                readJsonToObjects(searchJSON);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //get end time
            long endTime = System.currentTimeMillis();

            //calculate total time
            long totalTime = endTime - startTime;

            //print total time
            Log.d("object loading time", Long.toString(totalTime) + "ms");

            //update the card view
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Parses JSON file into objects
     *
     * @param file name of the JSON file to be parsed.
     */
    private void readJsonToObjects(String file) throws IOException, JSONException {
        //clear the current list of tweets
        users.clear();

        //create new jObject for reading the file
        JSONObject jObject = new JSONObject(file);
        System.out.println(jObject);

        //get statuses
        JSONArray jTweetArray = jObject.getJSONArray("users");

        System.out.println("jtweetarray " + jTweetArray);

        //loop through statuses
        for (int i = 0; i < jTweetArray.length(); i++) {
            JSONObject jUserObject = jTweetArray.getJSONObject(i);

            //get user
           // JSONObject jUserObject = jTweetObj.getJSONObject("user");
            String name = jUserObject.getString("name");
            String screen_name = jUserObject.getString("screen_name");
            String profile_image_url = jUserObject.getString("profile_image_url");

            //create new user object with extracted json data
            Users user = new Users(screen_name, name, profile_image_url);
            users.add(user);
        }
    }
}
