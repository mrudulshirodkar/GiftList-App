package edu.uncc.giftlistapp.models;

import java.io.Serializable;
import java.util.ArrayList;

import edu.uncc.giftlistapp.models.Gift;

public class GiftList implements Serializable {
    public String gid;
    public String name;
    public ArrayList<Gift> items;

    public GiftList() {
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Gift> getItems() {
        return items;
    }

    public void setItems(ArrayList<Gift> items) {
        this.items = items;
    }
}
