package lk.kavi.travelapp.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class RiskData implements Serializable {
    String name;
    String img;
    double lon;
    double lat;
    String description;
    String risk_pg;
    int death;
    Bitmap imgBitmap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Bitmap getImgBitmap() {
        return imgBitmap;
    }

    public void setImgBitmap(Bitmap imgBitmap) {
        this.imgBitmap = imgBitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getRisk_pg() {
        return risk_pg;
    }

    public void setRisk_pg(String risk_pg) {
        this.risk_pg = risk_pg;
    }

    public int getDeath() {
        return death;
    }

    public void setDeath(int death) {
        this.death = death;
    }
}
