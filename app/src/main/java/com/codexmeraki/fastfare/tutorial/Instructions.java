package com.codexmeraki.fastfare.tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.codexmeraki.fastfare.R;
import com.codexmeraki.fastfare.adapter.InstructionPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Instructions extends AppCompatActivity {

    private ViewPager screenPager;
    InstructionPagerAdapter instructionPagerAdapter ;
    String title = "";
    int position = 0;
    TabLayout tabIndicator;
    Animation btnAnim ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.app_green, getTheme())));
        View view = getLayoutInflater().inflate(R.layout.item_toolbar, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        ab.setCustomView(view, layout);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(10);

        setContentView(R.layout.activity_instructions);

        Intent intent = getIntent();

        title = intent.getStringExtra("title");
        position = intent.getIntExtra("position", 0);

        ((TextView)view.findViewById(R.id.toolbar_title)).setText("How to Use: "+title);
        ((TextView)view.findViewById(R.id.toolbar_title)).setTextSize(20);

        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        final List<ScreenItem> mList = new ArrayList<>();
        switch(getIntent().getIntExtra("position",-1)) {
            case 0:
                //Forgot Password
                mList.add(new ScreenItem("Forgot Password", "In the Login Screen, press \"Forgot Password\"", R.drawable.htu_00_00));
                mList.add(new ScreenItem("Forgot Password", "Enter your account\'s email address. An email will be sent containing a link to reset your password", R.drawable.htu_00_01));
                break;
            case 1:
                //Booking and Payment
                mList.add(new ScreenItem("Booking and Payment", "In the dashboard, select \"Pay Now\"", R.drawable.htu_01_00));
                mList.add(new ScreenItem("Booking and Payment", "Make sure that you accept and allow all permissions (Camera and Location).", R.drawable.htu_01_01));
                mList.add(new ScreenItem("Booking and Payment", "Point and Scan the driver's QR Code", R.drawable.htu_01_02));
                mList.add(new ScreenItem("Booking and Payment", "The system will determine you Origin. You will need to select which Destination you wish to go to.", R.drawable.htu_01_03));
                mList.add(new ScreenItem("Booking and Payment", "Review your bill and select \"Confirm\" to proceed with the booking process\nIf you wish to decline simply select \"Decline\"", R.drawable.htu_01_04));
                break;
            case 2:
                //TopUp
                mList.add(new ScreenItem("Top Up Balance", "There are two ways to top up. In the dashboard select the TopUp button next to the balance. While, in balance management it is under actions.", R.drawable.htu_02_00));
                mList.add(new ScreenItem("Top Up Balance", "Select a partner you wish to pay and process your request", R.drawable.htu_02_01));
                mList.add(new ScreenItem("Top Up Balance", "Enter the amount you will top up and select pay", R.drawable.htu_02_02));
                mList.add(new ScreenItem("Top Up Balance", "The system will then generate a barcode to be scanned by the partner you've selected.", R.drawable.htu_02_03));
                break;
            case 3:
                //RegisterDriver
                mList.add(new ScreenItem("Register as Driver", "Go to dashboard and select Apply as Driver", R.drawable.htu_03_00));
                mList.add(new ScreenItem("Register as Driver", "Fill out the form with your information then select \"Apply\"", R.drawable.htu_03_01));
                break;
            case 5:
                //ContactUS
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","tneck2020@gmail.com", null));
                email.putExtra(Intent.EXTRA_SUBJECT, "Support");
                email.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                finish();
                break;
        }
        mList.add(new ScreenItem("Got more questions?", "END-CONTACT", R.drawable.ic_question));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        instructionPagerAdapter = new InstructionPagerAdapter(this, mList);
        screenPager.setAdapter(instructionPagerAdapter);

        // setup tab-layout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // tab-layout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void loadLastScreen() {
        tabIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}