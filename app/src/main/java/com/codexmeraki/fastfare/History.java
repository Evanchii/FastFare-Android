package com.codexmeraki.fastfare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class History extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CommonFunctions cf;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("History");
        setContentView(R.layout.activity_history);

        cf = new CommonFunctions();
        cf.fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (CommonFunctions.menu(this, item, "History"))
            finish();
        return true;
    }
}