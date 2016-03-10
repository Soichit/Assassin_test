package edu.uw.jyinouye.assassin.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by iguest on 3/9/16.
 */
public class Ranking implements Comparable{

    private String email; // email of the user
    private String userName;
    private long kills;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    public Ranking() {
    }

    public Ranking(String email, String userName, long kills) {
        this.email = email;
        this.userName = userName;
        this.kills = kills;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return userName;
    }

    //@JsonIgnore
    public long getKills() {
        return kills;
    }

    //compare to to be able to sort the values in the table
    @Override
    public int compareTo(Object another) {
        Ranking r = (Ranking)another;
        return (int)r.getKills() - (int)this.kills;
    }

    //tosting to have the array adapter be called
    public String toString() {
        return this.userName + " kills: " + this.getKills();
    }
}