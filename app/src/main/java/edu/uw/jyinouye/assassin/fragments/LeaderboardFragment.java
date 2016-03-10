package edu.uw.jyinouye.assassin.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.uw.jyinouye.assassin.Assassin;
import edu.uw.jyinouye.assassin.Player;
import edu.uw.jyinouye.assassin.R;
import edu.uw.jyinouye.assassin.util.Chat;
import edu.uw.jyinouye.assassin.util.FirebaseListAdapter;
import edu.uw.jyinouye.assassin.util.Ranking;

/**
 *
 */
public class LeaderboardFragment extends Fragment {

    private static final String TAG = "LeaderboardFragment";
    private Firebase mPlayers; // gets the players from firebase
    private Firebase mPlayerGroup;
    private ListView listView; //gets a listview
    private ArrayAdapter LeaderboardAdapter; //is the adapter to store all the rankings
    private List<Ranking> rankings;
    //private ChatListAdapter mChatListAdapter;

    public LeaderboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        listView = (ListView) v.findViewById(R.id.leaderboard_listview);
        rankings = new ArrayList<Ranking>();
        //creates a new assassin activity
        Assassin assassin = (Assassin) getActivity().getApplication();
        mPlayers = assassin.getRef().child("players");

        //adds a value event lsitener to keep track of all the rankings
        mPlayers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rankings.clear(); // clears all the previous rankings
                LeaderboardAdapter.clear(); // creates an adapter
                for(DataSnapshot dater : dataSnapshot.getChildren()) {
                    String email = dater.child("email").getValue(String.class);
                    int kills = dater.child("kills").getValue(Integer.class);
                    String username = dater.child("username").getValue(String.class);
                    Ranking rank = new Ranking(email, username, kills);
                    rankings.add(rank);
                }

                //sorts and collects all the present rankings
                Collections.sort(rankings);
                for(int i = 0;i < rankings.size();i++) {
                    LeaderboardAdapter.insert((i+1)+": "+rankings.get(i),i);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        LeaderboardAdapter = new ArrayAdapter(getActivity(), R.layout.list_item,R.id.txtItem);
        Log.v(TAG, "mPlayers: " + mPlayers);
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        listView.setAdapter(LeaderboardAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
