package edu.uncc.giftlistapp.models;

public class Products{
    public String pid;
    public String name;
    public String img_url;
    public String price;

    @Override
    public String toString() {
        return "Products{" +
                "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", img_url='" + img_url + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public Products() {
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
