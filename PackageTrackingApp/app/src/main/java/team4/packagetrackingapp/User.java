package team4.packagetrackingapp;

import android.util.Log;

/**
 * Created by bogol on 27/3/18.
 */

public class User {
    private String name;
    private String contact_no;
    private String email_id;
    private String username;
    private String password;
    private String user_type;
    private String seller_id;

    User(String name, String contact_no, String email_id,
         String username, String password,
         String user_type,String seller_id) {
        this.name = name;
        this.contact_no = contact_no;
        this.email_id = email_id;
        this.username = username;
        this.password = password;
        this.user_type = user_type;
        this.seller_id =seller_id;
    }

    public void show() {
        Log.e("new user created", "with creds");
        Log.e("username", this.username);
        Log.e("password", this.password);
        Log.e("usertype", this.user_type);
    }
}
