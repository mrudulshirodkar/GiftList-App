package edu.uncc.giftlistapp.models;

import java.util.ArrayList;

public class GiftRoot {
    public String status;
    public ArrayList<GiftList> lists;

    public GiftRoot() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<GiftList> getLists() {
        return lists;
    }

    public void setLists(ArrayList<GiftList> lists) {
        this.lists = lists;
    }
}
