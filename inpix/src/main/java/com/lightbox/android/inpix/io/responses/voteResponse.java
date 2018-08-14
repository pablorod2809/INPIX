package com.lightbox.android.inpix.io.responses;

/**
 * Created by pablorodriguez on 22/6/18.
 */

public class voteResponse {

    private int userId;
    private int vote;

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getVote() { return vote; }
    public void setVote(int vote) { this.vote = vote; }

}
