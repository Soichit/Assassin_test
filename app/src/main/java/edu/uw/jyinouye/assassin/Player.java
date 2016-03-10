package edu.uw.jyinouye.assassin;

import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by colec on 3/6/2016.
 */
public class Player {

    private Firebase groupRef;

    private String uid;
    private String targetuid;
    private String email;
    private String userName;
    private String groupName;
    private int avatar;
    private Location location;
    private double latitude;
    private double longitude;
    private int kills;
    private long deaths;
    private long currency;
    private boolean isAdmin;
    public boolean isPlaying;
    public boolean isDead;

    private OnPlayerUpdatedListener mPlayerUpdatedListener;

    public Player() {}

    public Player(String uid, String email, String groupName, int avatar) {

        this.uid = uid; //user id of hte player
        this.email = email;// email of the current user
        this.groupName = groupName; // current name of the group
        this.avatar = avatar; //avator of the current player
        this.kills = 0; // kills
        this.deaths = 0; // deaths
        this.currency = 0;
        this.targetuid = "";
        this.isPlaying = true;
        this.isDead = false;
    }

    public long getCurrency() { return currency; }

    public int getKills() { return kills; }

    public long getDeaths() { return deaths; }

    public String getUid() { return uid; }

    public String getEmail() { return email; }

    public String getUserName() { return userName;}

    public String getGroupName() { return groupName; }

    public int getAvatar() { return avatar; }

    public String getTargetuid() { return targetuid; }

    @JsonIgnore
    public Location getLocation() { return location; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public boolean getAdmin() { return isAdmin; }

    public boolean getIsPlaying() { return isPlaying; }

    public boolean getIsDead() { return isDead; }

    public void setIsDead(boolean isDead) { this.isDead = isDead; }

    public void setRef(Firebase ref) {
        groupRef = ref;
    }

    public void incKill() {
        kills = kills + 1;
        currency = currency + 5;
    }

    public void setisPlaying(boolean isPlaying2) {
        isPlaying = isPlaying2;
    }

    public void incDeath() { deaths = deaths + 1; }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTargetuid(String tuid) {
        this.targetuid = tuid;
    }

    public void setTarget(String tuid) {
        groupRef.child("players").child(uid).child("targetuid").setValue(tuid);
        this.targetuid = tuid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public void setCurrency(long currency) {
        this.currency = currency;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocation(Location location) {
        Log.v("PlayerObject", "set location: " + location.getLatitude() + " " + location.getLongitude());
        groupRef.child("players").child(uid).child("latitude").setValue((double) location.getLatitude());
        groupRef.child("players").child(uid).child("longitude").setValue((double) location.getLongitude());
        this.location = location;
    }

    //interface allows Assassin class to listen for changes in player location, send them to firebase
    public interface OnPlayerUpdatedListener {
        void onPlayerLocationChanged(Location location);
    }
}
