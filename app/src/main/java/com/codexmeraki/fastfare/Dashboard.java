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

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    CommonFunctions cf;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("FastFare");
        setContentView(R.layout.activity_dashboard);

        cf = new CommonFunctions();
        cf.fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

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
        if (CommonFunctions.menu(this, item, "Dashboard"))
            finish();
        return true;
    }

    public void btnApply(View view) {
        startActivity(new Intent(view.getContext(), DriverApplication.class));
    }

    public void btnUpdate(View view) {
        startActivity(new Intent(view.getContext(), DriverApplication.class).putExtra("mode", 1));
    }

    public void btnBook(View view) {
        startActivity(new Intent(view.getContext(), BookScan.class));
    }

    public void btnBalance(View view) {
        startActivity(new Intent(view.getContext(), BalanceManagement.class));
        finish();
    }

    public void btnTopUp(View view) {
        startActivity(new Intent(view.getContext(), TopUp.class));
    }

    public void btnGetStarted(View view) {
        startActivity(new Intent(view.getContext(), DriverGetStarted.class));
    }
}