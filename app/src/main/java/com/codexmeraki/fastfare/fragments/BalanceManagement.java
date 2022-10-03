package com.codexmeraki.fastfare.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codexmeraki.fastfare.App;
import com.codexmeraki.fastfare.R;
import com.codexmeraki.fastfare.balance.TopUp;
import com.codexmeraki.fastfare.balance.Transfer;
import com.codexmeraki.fastfare.databinding.ActivityBalanceManagementBinding;
import com.codexmeraki.fastfare.adapter.BalanceAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BalanceManagement extends Fragment {

    Thread bgFetch;
    private ActivityBalanceManagementBinding binding;
    private CardView transfer, topUp;
    private RecyclerView transactions;
    private TextView balance;

    private String uid;
    private SharedPreferences sp;
    private OkHttpClient client;
    private Toast popUp;
    TreeMap<String, HashMap<String, String>> data;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityBalanceManagementBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        transfer = root.findViewById(R.id.balance_cardTransfer);
        topUp = root.findViewById(R.id.balance_cardTopUp);
        transactions = root.findViewById(R.id.balance_transactions);
        balance = root.findViewById(R.id.balance_txtBalance);

        transfer.setOnClickListener(view -> startActivity(new Intent(getActivity(), Transfer.class)));
        topUp.setOnClickListener(view -> startActivity(new Intent(getActivity(), TopUp.class)));

        client = new OkHttpClient();
        sp = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");

        popUp = Toast.makeText(this.getActivity(), "", Toast.LENGTH_LONG);
        getData();

        return root;
    }

    private void getData() {
        //get balance

        //get BalanceTransactions Data
        bgFetch = new Thread(() -> {
            FormBody fbFetch = new FormBody.Builder()
                    .add("uid", uid)
                    .build(),
                    fbBalance = new FormBody.Builder()
                            .add("uid", uid)
                            .build();

            Request reqFetch = new Request.Builder()
                    .url(App.APP_URL + "/android/fetchTransactions.php")
                    .post(fbFetch)
                    .build(),
                    reqBalance = new Request.Builder()
                            .url(App.APP_URL + "/android/fetchBalance.php")
                            .post(fbBalance)
                            .build();

            try {
                Response resFetch = client.newCall(reqFetch).execute();
                String strFetch = resFetch.body().string();
                Log.d("BM:",strFetch);
                JSONObject rData = new JSONObject(strFetch);

                if (rData.has("data")) {

                    //clean data
                    //initialize all required variables
                    data = new TreeMap<>(Collections.reverseOrder());
                    HashMap<String, String> tmp;

                    if (rData.get("data").getClass().getSimpleName().equals("JSONObject")) {

                        JSONObject rd1 = (JSONObject) rData.get("data"), rd2, rd3;
                        //iterate each entry
                        for (Iterator<String> it = rd1.keys(); it.hasNext(); ) {
                            String key = it.next(); //Main Keys [ts]

                            //init required variables: sub-level 1
                            tmp = new HashMap<>();
                            rd2 = (JSONObject) rd1.get(key);

                            String[] keys = {"timestamp", "amount", "sender", "receiver", "type", "status"};
                            for (String k : keys) tmp.put(k, rd2.getString(k));

                            keys = new String[]{"firstname", "middlename", "lastname"};
                            rd3 = (JSONObject) rd2.get("receiverData");
                            String name = "";
                            for (String k : keys) name += rd3.getString(k) + " ";
                            tmp.put("recName", name);

                            if (rd2.has("senderData")) {
                                rd3 = (JSONObject) rd2.get("senderData");
                                name = "";
                                for (String k : keys) name += rd3.getString(k) + " ";
                                tmp.put("senName", name);
                            } else {
                                tmp.put("senName", rd2.getString("sender"));
                            }

                            try {
                                Date dt = new Date((long) Integer.valueOf(key) * 1000);
                                SimpleDateFormat dt1 = new SimpleDateFormat("MMMM dd, yyyy HH:mm aa");
                                tmp.put("timestamp", dt1.format(dt));
                            } catch (Exception e) {
                                Log.d("Balance>", e.getMessage());
                            }

                            data.put(key, tmp);
                        }
                    } else {
                        tmp = new HashMap<>();
                        data.put("0", tmp);
                    }

                    this.getActivity().runOnUiThread(() -> {
                        try {
                            LinearLayoutManager llm = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            transactions.setLayoutManager(llm);

                            BalanceAdapter adapter = new BalanceAdapter(this.getActivity(), data, uid);
                            transactions.setAdapter(adapter);
                        } catch (NullPointerException e) {
                            Log.e("Balance", "An error has occured. Details: ");
                            e.printStackTrace();
                        }
                    });
                } else {
                    String err = rData.has("error") ? rData.get("error").toString() : "An error has occurred.";
                    popUp.setText(err);
                    popUp.show();
                }

                Response resBalance = client.newCall(reqBalance).execute();
                JSONObject jsonBal = new JSONObject(resBalance.body().string());
                if (jsonBal.has("balance")) {
                    this.getActivity().runOnUiThread(() -> {
                        try {
                            Locale locale = new Locale("en", "PH");
                            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

                            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
                            decimalFormatSymbols.setCurrencySymbol("");
                            ((DecimalFormat) currencyFormatter).setDecimalFormatSymbols(decimalFormatSymbols);

                            Log.d("RESTBalance", jsonBal.getString("balance"));
                            balance.setText(currencyFormatter.format(Double.valueOf(jsonBal.getString("balance"))).trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    String err = jsonBal.has("error") ? rData.get("error").toString() : "An error has occurred.";
                    popUp.setText(err);
                    popUp.show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });

        bgFetch.start();
    }

    @Override
    public void onPause() {
        if (bgFetch.isAlive()) bgFetch.interrupt();
        super.onPause();
    }
}