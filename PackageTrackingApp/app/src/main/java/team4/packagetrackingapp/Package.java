package team4.packagetrackingapp;

import android.util.Log;

public class Package {

    private String id;
    private String destination;

    Package(String id, String destination) {
        this.id = id;
        this.destination = destination;
    }

    public void show() {
        Log.e("new package created", "with creds");
        Log.e("id", this.id);
        Log.e("destination", this.destination);

    }
}
