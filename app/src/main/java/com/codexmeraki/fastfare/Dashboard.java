package com.codexmeraki.fastfare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class Dashboard extends Fragment {

    RelativeLayout balance;
    AppCompatButton topUp, book, apply, getStarted, update;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_dashboard, container, false);

        balance = mView.findViewById(R.id.dashboard_balance);
        topUp = mView.findViewById(R.id.dashboard_btnTopUp);
        book = mView.findViewById(R.id.dashboard_btnTravel);
        apply = mView.findViewById(R.id.dashboard_btnApply);
        getStarted = mView.findViewById(R.id.dashboard_btnGetStarted);
        update = mView.findViewById(R.id.dashboard_btnUpdate);

        balance.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverApplication.class)));
        topUp.setOnClickListener(view -> startActivity(new Intent(view.getContext(), TopUp.class)));
        book.setOnClickListener(view -> startActivity(new Intent(view.getContext(), BookScan.class)));
        apply.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverApplication.class)));
        getStarted.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverGetStarted.class)));
        update.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverApplication.class).putExtra("mode", 1)));

        return mView;
    }
}