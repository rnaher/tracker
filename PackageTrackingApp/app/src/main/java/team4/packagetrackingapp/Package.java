package team4.packagetrackingapp;

import android.util.Log;

public class Package {

    private int packageID;
    private String destination;

    Package(int id, String destination) {
        this.packageID = id;
        this.destination = destination;
    }

    public void show() {
        Log.e("new package created", "with creds");
        Log.e("id", Integer.toString(this.packageID));
        Log.e("destination", this.destination);

    }
}
