package com.codexmeraki.fastfare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codexmeraki.fastfare.databinding.ActivityProfileBinding;
import com.codexmeraki.fastfare.databinding.ActivitySettingsBinding;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class Settings extends Fragment {

    private ActivitySettingsBinding binding;

    AppCompatButton profile, email, password, logout, htu, tos, pp;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ActivitySettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sp = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        profile = root.findViewById(R.id.settings_btnAccount);
        email = root.findViewById(R.id.settings_btnEmail);
        password = root.findViewById(R.id.settings_btnPassword);
        logout = root.findViewById(R.id.settings_btnLogout);
        htu = root.findViewById(R.id.settings_btnHTU);
        tos = root.findViewById(R.id.settings_btnToS);
        pp = root.findViewById(R.id.settings_btnPP);

        profile.setOnClickListener(view -> startActivity(new Intent(getActivity(), Profile.class)));
        email.setOnClickListener(view -> startActivity(new Intent(getActivity(), UpdateEmail.class)));
        password.setOnClickListener(view -> startActivity(new Intent(getActivity(), ChangePassword.class)));
        logout.setOnClickListener(view -> {
            sp.edit().clear().apply();
            startActivity(new Intent(getActivity(), Login.class));
            getActivity().finish();
        });
//        htu.setOnClickListener(view -> startActivity(new Intent(getActivity(), .class)));

//        TODO: Change URL to CodexMeraki|FastFare
        tos.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://react-app.ga/pages/terms.php/"))));

//        TODO: Change URL to CodexMeraki|FastFare
        pp.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://react-app.ga/pages/privacy.php/"))));

        return root;
    }
}