package edu.uw.jyinouye.assassin;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.jyinouye.assassin.fragments.ChatFragment;
import edu.uw.jyinouye.assassin.fragments.LeaderboardFragment;
import edu.uw.jyinouye.assassin.fragments.ProfileFragment;
import edu.uw.jyinouye.assassin.fragments.ShopFragment;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MainActivity";

    private Toolbar toolbar;
    private DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem mLastMenuItem;
    private Button killButton;
    private boolean winnerFlag;

    private SupportMapFragment mMapFragment;
    private ChatFragment mChatFragment;
    private LeaderboardFragment mLeaderboardFragment;
    private ProfileFragment mProfileFragment;
    private ShopFragment mShopFragment;
    //private ProfileFragment fragInfo;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private static final int LOC_REQUEST_CODE = 0;

    private Assassin assassin;
    private Player player;
    private Map<String, Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //ImageView overlay = (ImageView)findViewById(R.id.main_overlay);
        //overlay.setBackgroundColor(Color.argb(127, 213, 0, 0));
        //overlay.setVisibility(View.GONE);

        assassin = ((Assassin)getApplicationContext()).getInstance();
        player = assassin.getPlayer();
        Log.v(TAG, "Player email: " + player.getEmail());
        players = new HashMap<>();
        winnerFlag = true;

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Find our drawer view
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        // Setup drawer view
        setupDrawerContent(nvDrawer);

        // Poll for location every 10 seconds, max 5 seconds
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();

        // create references to fragments to add later
        mChatFragment = new ChatFragment();
        mLeaderboardFragment = new LeaderboardFragment();
        mProfileFragment = new ProfileFragment();
        mShopFragment = new ShopFragment();

        // create new mapfragment with callbacks to this activity
        mMapFragment = SupportMapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        // Setup initial state where all but mapfragment is hidden
        FragmentManager fragmentManager = getSupportFragmentManager();

        mProfileFragment = new ProfileFragment();
        fragmentManager
        .beginTransaction()
                .add(R.id.flContent, mMapFragment)
                .add(R.id.flContent, mChatFragment)
                .add(R.id.flContent, mLeaderboardFragment)
                .add(R.id.flContent, mProfileFragment)
                .add(R.id.flContent, mShopFragment)
                .hide(mChatFragment)
                .hide(mLeaderboardFragment)
                .hide(mProfileFragment)
                .hide(mShopFragment)
                .show(mMapFragment)
                .commit();

        //Set status bar to transparent for COOL effects!!~~~~~s
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }
        killButton = (Button) findViewById(R.id.kill_button);
        killButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean result = assassin.killPressed();
                if(result) {
                   // mLeaderboardFragment.refresh();
                }
                Log.v("hi", assassin.getPlayer().getKills()+"");
            }
        });

        getPlayerList();
    }

    /**
     * All code relating to drawer layout implementaion came from
     * https://guides.codepath.com/android/Fragment-Navigation-Drawer
     */

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //JASON, does this affect the start button???
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case R.id.admin_start_game:
                startGame();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });

        //Set up hamburger menu
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.nav_open, R.string.nav_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(drawerView.getWindowToken(), 0);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawer.addDrawerListener(mDrawerToggle);

        mLastMenuItem = nvDrawer.getMenu().findItem(R.id.nav_map_fragment);

        //OnClickListener for profile section
        View headerView = nvDrawer.inflateHeaderView(R.layout.nav_drawer_header);
        TextView profile_name = (TextView) headerView.findViewById(R.id.profile_name_text);
        String username = assassin.getPlayer().getUserName();
        if(username != null) {
            profile_name.setText(username);
        } else {
            profile_name.setText("Username not set :(");
        }
        TextView profile_email = (TextView) headerView.findViewById(R.id.profile_email_text);
        profile_email.setText(assassin.getPlayer().getEmail());
        setProfileImage(headerView);

        View profileView = headerView.findViewById(R.id.chosen_account_content_view);
        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.show(mProfileFragment)
                        .hide(mChatFragment)
                        .hide(mLeaderboardFragment)
                        .hide(mMapFragment)
                        .hide(mProfileFragment)
                        .commit();
                setTitle("Profile");
                mDrawer.closeDrawers();
                selectDrawerItem(null);
            }
        });
    }

    //sets profile avatar
    public void setProfileImage(View headerView) {
        int selectedAvatar = assassin.getPlayer().getAvatar();
        ImageView profile_image = (ImageView) headerView.findViewById(R.id.profile_image);
        Log.v(TAG, "selectedAvatar: " + selectedAvatar);

        //allows user to select their avatar
        if (selectedAvatar == 1) {
            profile_image.setImageResource(R.drawable.avator1);
        } else if (selectedAvatar == 2) {
            profile_image.setImageResource(R.drawable.avator2);
        } else if (selectedAvatar == 3) {
            profile_image.setImageResource(R.drawable.avator3);
        } else if (selectedAvatar == 4) {
            profile_image.setImageResource(R.drawable.avator4);
        } else if (selectedAvatar == 5) {
            profile_image.setImageResource(R.drawable.avator5);
        } else if (selectedAvatar == 6) {
            profile_image.setImageResource(R.drawable.avator6);
        } else if (selectedAvatar == 7) {
            profile_image.setImageResource(R.drawable.avator7);
        } else if (selectedAvatar == 8) {
            profile_image.setImageResource(R.drawable.avator8);
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        if(menuItem == null) {
            if(mLastMenuItem != null) {
                mLastMenuItem.setChecked(false);
            }
            return;
        }
        mLastMenuItem = menuItem;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        //menu items
        //each case hides the non active fragments and shows the selected fragment
        switch(menuItem.getItemId()) {
            case R.id.nav_map_fragment:
                ft.show(mMapFragment)
                        .hide(mChatFragment)
                        .hide(mLeaderboardFragment)
                        .hide(mProfileFragment);
                killButton.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_chat_fragment:
                ft.show(mChatFragment)
                        .hide(mMapFragment)
                        .hide(mLeaderboardFragment)
                        .hide(mProfileFragment);
                killButton.setVisibility(View.GONE);
                break;
            case R.id.nav_profile_fragment:
                ft.show(mProfileFragment)
                        .hide(mChatFragment)
                        .hide(mMapFragment)
                        .hide(mLeaderboardFragment);
                killButton.setVisibility(View.GONE);
                break;
            case R.id.nav_leaderboard_fragment:
                ft.show(mLeaderboardFragment)
                        .hide(mChatFragment)
                        .hide(mMapFragment)
                        .hide(mProfileFragment);
                killButton.setVisibility(View.GONE);
                break;
            default:
                ft.show(mMapFragment)
                        .hide(mChatFragment)
                        .hide(mLeaderboardFragment)
                        .hide(mProfileFragment);
                killButton.setVisibility(View.GONE);
                break;
        }
        ft.commit();

        // Highlight the selected item, update the title, and close the drawer
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        // Set map ui elements
        UiSettings mapUiSettings = mMap.getUiSettings();
        mapUiSettings.setMapToolbarEnabled(true);
        mapUiSettings.setZoomControlsEnabled(true);
        mapUiSettings.setMyLocationButtonEnabled(true);

        // Show location and zoom
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(47.6097, -122.3331)));
            mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        } else {
            requestPermission();
        }
    }

    // Handles conversion between Location and LatLng
    private LatLng toLatLng(Location l) {
        return new LatLng(l.getLatitude(), l.getLongitude());
    }

    // Requests for permission to use ACCESS_FINE_LOCATION
    private void requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //TODO: Explain why you need the permission
        }
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOC_REQUEST_CODE);
    }

    // Updates location
    protected void startLocationUpdates() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        } else {
            requestPermission();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        //update location while the player is still playing and havn't died yet
        if(!player.getIsDead() && player.getIsPlaying()) {
            player.setLocation(location);
            mLastLocation = location;

            //update the location
            updatePlayerMarkers();
        }
    }

    //starts the game
    private void startGame() {

        //makes a list
        List<Player> players = new ArrayList<>();

        //adds all the players to the list
        for(Player p : this.players.values()) {
            players.add(p);
            p.setRef(assassin.getGroup());
        }

        // shuffle players,
        Collections.shuffle(players);
        for(int i = 0; i < players.size() - 1; i++) {
            if(players.get(i + 1).getUid() == null) {
                Log.v(TAG, "Null");
            }
            players.get(i).setTarget(players.get(i + 1).getUid());
        }
        players.get(players.size() - 1).setTarget(players.get(0).getUid());
        Log.v(TAG, "Last player in chain: " + players.get(players.size() - 1).getEmail());
    }

    //stops the game
    private void stopGame() {
        mMap.clear();
    }

    //updates the markers on google maps
    private void updatePlayerMarkers() {
        mMap.clear();
        Collection<Player> playersCopy = players.values();
        Log.v(TAG, "Attempting to update player markers. #Players = " + playersCopy.size());

        //update each player in the group
        for(Player p : playersCopy) {
            if (!p.getEmail().equals(player.getEmail())) {
                Log.v(TAG, "Target: " + player.getTargetuid());
                if(player.getTargetuid().equals(p.getUid())) {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))
                            .title(p.getEmail())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target_marker))
                    );
                    checkRange(p);
                } else {
                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(p.getLatitude(), p.getLongitude()))
                            .title(p.getEmail())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_player_marker))
                    );
                }
                Log.v(TAG, "Player: " + p.getEmail() + ", location " + p.getLatitude() + ", " + p.getLongitude());
            }
        }
    }

    //checks to let user know if the target is in range
    private void checkRange(Player p) {
        Location l = new Location("");
        l.setLatitude(p.getLatitude());
        l.setLongitude(p.getLongitude());
        float distance = l.distanceTo(mLastLocation);

        // if distance is less than 15 meters allow user to kill the other player
        if(distance < 15) {
            Toast toast = Toast.makeText(this, p.getEmail() + " is in range", Toast.LENGTH_LONG);
            toast.show();
            killButton.setEnabled(true);
        } else {

            //disallow player to kill other play due to range being too high
            killButton.setEnabled(false);
        }
    }

    public void getPlayerList(){
        //query firebase for all players
        final Firebase groupRef = assassin.getGroup();
        groupRef.child("players").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Player player = dataSnapshot.getValue(Player.class);

                //get coordinates
                Object lat = dataSnapshot.child("latitude").getValue();
                Object lng = dataSnapshot.child("longitude").getValue();

                //sets valid coordinates
                if (lat != null && lng != null) {
                    player.setLatitude((double) lat);
                    player.setLongitude((double) lng);
                }

                //places coordinates in database
                players.put(dataSnapshot.getKey(), player);
                if(mLastLocation != null) {
                    onLocationChanged(mLastLocation);
                }
            }

            @Override
            public void onChildChanged(final DataSnapshot dataSnapshot, String s) {
                Player databasePlayer = dataSnapshot.getValue(Player.class);

                //get the current latitude and longitude
                Object lat = dataSnapshot.child("latitude").getValue();
                Object lng = dataSnapshot.child("longitude").getValue();

                //set the position in the database with valid coordinates
                if (lat != null && lng != null) {
                    databasePlayer.setLatitude((double) lat);
                    databasePlayer.setLongitude((double) lng);
                    databasePlayer.setTargetuid(databasePlayer.getTargetuid());
                    players.put(dataSnapshot.getKey(), databasePlayer);
                }

                //loser case
                // if changed player is you
                if(databasePlayer != null && databasePlayer.getUid().equals(player.getUid())) {
                    player.setTargetuid(databasePlayer.getTargetuid());
                    // if player is dead
                    if(databasePlayer.getIsDead()) {
                        dataSnapshot.getRef().child("isDead").setValue(true);
                        player.setIsDead(true);
                        dataSnapshot.getRef().setValue(null);

                        //player has lost the game
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("You were killed!")
                                .setMessage("DEAD")
                                .setPositiveButton("Leave game", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSnapshot.getRef().setValue(null);
                                        finish();
                                    }
                                })
                                .create();
                        dialog.show();

                    }
                }

                //Winner if last player
            }

            @Override
            public void onChildRemoved(final DataSnapshot dataSnapshot) {
                players.remove(dataSnapshot.getKey());
                Log.v(TAG, "Target: " + player.getTargetuid());
                // check if player is last remaining
                if(winnerFlag && player.getTargetuid().equals(player.getUid())) {
                    winnerFlag = false;

                    //alert user that they are the winner of the game
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("You are the winner!")
                            .setMessage("Congratulation")
                            .setPositiveButton("End game", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSnapshot.getRef().setValue(null);
                                    stopGame();
                                }
                            })
                            .create();
                    dialog.show();
                    Log.v(TAG, dialog.toString());
                }

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {

        //allows access to the users location
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        } else {
            requestPermission();
        }

        // Move the camera to you
        if (mLastLocation != null) {
            LatLng lastPos = toLatLng(mLastLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastPos));
        }

        // make sure we poll for updates
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            // Granted permission, continue
            case LOC_REQUEST_CODE:
                onConnected(null);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        //remove player from the game
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //assassin.getGroup().child("players").child(player.getUid()).child("isPlaying").setValue(false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //allows player to join a game
        assassin.getGroup().child("players").child(player.getUid()).child("isPlaying").setValue(true);
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
