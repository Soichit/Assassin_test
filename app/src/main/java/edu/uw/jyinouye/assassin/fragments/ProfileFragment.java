package edu.uw.jyinouye.assassin.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.uw.jyinouye.assassin.Assassin;
import edu.uw.jyinouye.assassin.Player;
import edu.uw.jyinouye.assassin.R;

/**
 * Displays stats for a player
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private Player player;

    public ProfileFragment() {
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
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        Assassin assassin = (Assassin)getActivity().getApplication();
        player = assassin.getPlayer();

        //get all of the textViews
        TextView name = (TextView) rootView.findViewById(R.id.profile_name);
        TextView kills = (TextView) rootView.findViewById(R.id.profile_kills);
        TextView deaths = (TextView) rootView.findViewById(R.id.profile_deaths);
        TextView currency = (TextView) rootView.findViewById(R.id.profile_currency);
        setProfileImage(rootView);

        //set all of the textViews
        name.setText(player.getUserName());
        kills.setText(player.getKills() + "");
        deaths.setText(player.getDeaths() + "");
        currency.setText(player.getCurrency() + "");

        /*
        String targetUid = player.getTargetuid();
        Log.v(TAG, "Uber targetUid: " + targetUid);
        Firebase target = assassin.getGroup().child("players").child(player.getTargetuid());
        Log.v(TAG, "Santa target: " + target);

        target.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG, "Pizza: " + dataSnapshot.getValue());
                //int selectedAvatar = (int) dataSnapshot.getValue();
                //Log.v(TAG, "Mario selectedAvatar: " + selectedAvatar);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        */

        return rootView;
    }

    //sets profile avatar
    public void setProfileImage(View rootView) {
        int selectedAvator = player.getAvatar();
        ImageView profile_image = (ImageView) rootView.findViewById(R.id.profile_image);
        if (selectedAvator == 1) {
            profile_image.setImageResource(R.drawable.avator1);
        } else if (selectedAvator == 2) {
            profile_image.setImageResource(R.drawable.avator2);
        } else if (selectedAvator == 3) {
            profile_image.setImageResource(R.drawable.avator3);
        } else if (selectedAvator == 4) {
            profile_image.setImageResource(R.drawable.avator4);
        } else if (selectedAvator == 5) {
            profile_image.setImageResource(R.drawable.avator5);
        } else if (selectedAvator == 6) {
            profile_image.setImageResource(R.drawable.avator6);
        } else if (selectedAvator == 7) {
            profile_image.setImageResource(R.drawable.avator7);
        } else if (selectedAvator == 8) {
            profile_image.setImageResource(R.drawable.avator8);
        }
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
