package com.codexmeraki.fastfare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import com.google.android.material.navigation.NavigationView;

public class CommonFunctions {

    @SuppressLint("NonConstantResourceId")
    public static boolean menu(Context con, MenuItem item, String src) {
        Intent i = null;
//        mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Dashboard.class);
                break;
            case R.id.action_balance:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, BalanceManagement.class);
                break;
            case R.id.action_history:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, History.class);
                break;
            case R.id.action_settings:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Settings.class);
                break;
            case R.id.action_logout:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Login.class);
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails (NavigationView nv) {

    }
}
