package com.github.lyue.bmwchallenge;

/**
 * Created by yue_liang on 2017/4/5.
 */

public class JavaBean {
    /**
     * ID : 32870
     * Name : Doughnut Vault Canal
     * Latitude : 41.883976
     * Longitude : -87.639346
     * Address : 11 N Canal St L30 Chicago, IL 60606
     * ArrivalTime : 2017-04-05T21:57:13.517
     */

    private int ID;
    private String Name;
    private double Latitude;
    private double Longitude;
    private String Address;
    private String ArrivalTime;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double Latitude) {
        this.Latitude = Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double Longitude) {
        this.Longitude = Longitude;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String ArrivalTime) {
        this.ArrivalTime = ArrivalTime;
    }

    @Override
    public String toString() {
        String str = String.format("Location : %s\n" +
                "ArrivalTime : %s\n" +
                "Address : %s\n" +
                "Latitude : %s\n" +
                "Longitude : %s\n", getName(),
                getArrivalTime(),
                getAddress(),
                getLatitude(),
                getLongitude());
        return str;
    }
}
