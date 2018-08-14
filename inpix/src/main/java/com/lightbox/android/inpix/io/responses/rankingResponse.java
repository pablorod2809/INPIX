package com.lightbox.android.inpix.io.responses;

import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.lightbox.android.inpix.R;

import java.util.List;

/**
 * Created by pablorodriguez on 20/6/18.
 */

public class rankingResponse {
    private String event;
    @SerializedName("ranking")
    private List<imageRankValue> ranking;

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public List<imageRankValue> getRanking() {
        return ranking;
    }

    public void setRanking(List<imageRankValue> images) {
        this.ranking = images;
    }

    public class imageRankValue{
        private int id;
        private String image;
        private String user;
        private int votes;
        private int my_vote;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImages(String images) {
            this.image = images;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }


        public int getMyVote() {
            return my_vote;
        }

        public void setMyVote() {
            if (this.my_vote == 0) {
                this.my_vote = 1;
                this.votes = this.votes + 1;
            }else{
                this.my_vote = 0;
                this.votes = this.votes - 1;
            }
        }
    }
}

