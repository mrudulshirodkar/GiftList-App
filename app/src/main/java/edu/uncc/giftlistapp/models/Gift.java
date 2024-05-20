package edu.uncc.giftlistapp.models;

public class Gift {
    public String pid;
    public int count;
    public String name;
    public String price_per_item;
    public String img_url;

    public void incrementCount(){
        count++;
    }
    public void decrementCount(){
        if(count > 0){
            count --;
        }
    }
    public Gift() {
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice_per_item() {
        return price_per_item;
    }

    public void setPrice_per_item(String price_per_item) {
        this.price_per_item = price_per_item;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
