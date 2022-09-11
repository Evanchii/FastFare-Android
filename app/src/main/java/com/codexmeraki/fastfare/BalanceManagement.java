package com.codexmeraki.fastfare;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.codexmeraki.fastfare.databinding.ActivityBalanceManagementBinding;

public class BalanceManagement extends Fragment {

    private ActivityBalanceManagementBinding binding;
    private CardView transfer, topUp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityBalanceManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        transfer = root.findViewById(R.id.balance_cardTransfer);
        topUp = root.findViewById(R.id.balance_cardTopUp);

        transfer.setOnClickListener(view -> startActivity(new Intent(getActivity(), Transfer.class)));
        transfer.setOnClickListener(view -> startActivity(new Intent(getActivity(), TopUp.class)));

        return root;
    }
}