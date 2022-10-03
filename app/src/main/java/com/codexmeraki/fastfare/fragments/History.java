package com.codexmeraki.fastfare.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codexmeraki.fastfare.App;
import com.codexmeraki.fastfare.R;
import com.codexmeraki.fastfare.databinding.ActivityHistoryBinding;
import com.codexmeraki.fastfare.adapter.HistoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class History extends Fragment {

    private ActivityHistoryBinding binding;
    RecyclerView history;
    Thread bgHistory;

    OkHttpClient client = new OkHttpClient();
    String uid;
    SharedPreferences sp;
    Toast popUp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ActivityHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        history = root.findViewById(R.id.history_rv);

        sp = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        uid = sp.getString("uid", "");
        popUp = Toast.makeText(this.getActivity(), "", Toast.LENGTH_LONG);

        fetchAllData();

        return root;
    }

    private void fetchAllData() {
        bgHistory = new Thread(() -> {
            FormBody formBody = new FormBody.Builder()
                    .add("uid", uid)
                    .build();

            Request requestHistory = new Request.Builder()
                    .url(App.APP_URL + "/android/fetchHistory.php")
                    .post(formBody)
                    .build();

            try {
                Response responseHis = client.newCall(requestHistory).execute();
                JSONObject dataHistory = new JSONObject(responseHis.body().string());
                Log.d("RESTHistory", String.valueOf(dataHistory.keys()));
                if (dataHistory.has("data")) {
//                        Do RV stuff NOTE: LIMIT TO ONLY 5 ENTRIES TY
                    TreeMap<String, HashMap<String, String>> data = new TreeMap<>(Collections.reverseOrder());
                    if (dataHistory.get("data").getClass().getSimpleName().equals("JSONObject")) {
                        for (Iterator<String> it = ((JSONObject) dataHistory.get("data")).keys(); it.hasNext(); ) {
                            String key = it.next();
                            HashMap<String, String> tmp = new HashMap<>();
                            for (Iterator<String> iter = ((JSONObject) ((JSONObject) dataHistory.get("data")).get(key)).keys(); iter.hasNext(); ) {
                                String key2 = iter.next();
                                if (key2.equals("timestamp"))
                                    try {
                                        Date dt = new Date((long) Integer.valueOf(key) * 1000);
                                        SimpleDateFormat dt1 = new SimpleDateFormat("MMMM dd, yyyy HH:mm aa");
                                        tmp.put("timestamp", dt1.format(dt));
                                    } catch (Exception e) {
                                        Log.d("DashHis>", e.getMessage());
                                    }

                                else if (key2.equals("amount")) {
                                    Locale locale = new Locale("en", "PH");
                                    NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                                    tmp.put(key2, currencyFormatter.format(Double.valueOf(((JSONObject) ((JSONObject) dataHistory
                                            .get("data")).get(key)).get(key2).toString())));
                                } else
                                    tmp.put(key2, ((JSONObject) ((JSONObject) dataHistory.get("data")).get(key)).get(key2).toString());
                            }

                            data.put(key, tmp);
                        }
                    } else {
                        HashMap<String, String> tmp = new HashMap<>();
                        data.put("0", tmp);
                    }

                    getActivity().runOnUiThread(() -> {
                        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        history.setLayoutManager(llm);

                        HistoryAdapter adapter = new HistoryAdapter(getActivity(), data, uid);
                        history.setAdapter(adapter);
                    });
                } else {
                    String msg = dataHistory.has("error") ? dataHistory.get("error").toString() : "Something went wrong";
                    Log.d("REST", msg);
                    popUp.setText(msg);
                    popUp.show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });

        bgHistory.start();
    }

    @Override
    public void onPause() {
        if (bgHistory.isAlive()) bgHistory.interrupt();
        super.onPause();
    }
}