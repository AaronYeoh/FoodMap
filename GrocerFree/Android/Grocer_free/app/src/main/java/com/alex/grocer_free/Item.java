package com.alex.grocer_free;

/**
 * Created by alex on 16/07/15.
 */
public class Item {
    private int id;
    private String itemType;
    private double lat;
    private double lng;
    private String desc;


    public Item(){
    }
    public Item(int id, double lat, double lng, String itemType, String desc){
        this.id = id;
        this.lat = lat;
        this.lng = lng;
        this.itemType = itemType;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
