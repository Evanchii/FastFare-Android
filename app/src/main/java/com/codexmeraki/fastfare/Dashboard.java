package com.codexmeraki.fastfare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codexmeraki.fastfare.recyclerAdapter.HistoryAdapter;

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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Dashboard extends Fragment {

    Thread threadHistory, thread;
    final int TIMESTAMP = 0, PASSENGER_UID = 1, DRIVER_UID = 2, PRICE = 3, DISTANCE = 4, ORIGIN = 5, DESTINATION = 6, DRIVER_FIRSTNAME = 7, DRIVER_MIDDLENAME = 8, DRIVER_LASTNAME = 9, DRIVER_PLATE_NUMBER = 10, PASSENGER_FIRSTNAME = 11, PASSENGER_MIDDLENAME = 12, PASSENGER_LASTNAME = 13;
    RelativeLayout balance;
    AppCompatButton topUp, book, apply, getStarted, update;
    RecyclerView history;
    TextView balText;
    LinearLayout application, process, driver;

    SharedPreferences sharedPreferences;
    OkHttpClient client = new OkHttpClient();
    String msg = "", uid;
    Toast popUp;

    HistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_dashboard, container, false);

        sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        balance = mView.findViewById(R.id.dashboard_balance);
        topUp = mView.findViewById(R.id.dashboard_btnTopUp);
        book = mView.findViewById(R.id.dashboard_btnTravel);
        apply = mView.findViewById(R.id.dashboard_btnApply);
        getStarted = mView.findViewById(R.id.dashboard_btnGetStarted);
        update = mView.findViewById(R.id.dashboard_btnUpdate);

        balText = mView.findViewById(R.id.dashboard_txtBalance);
        history = mView.findViewById(R.id.dashboard_history);

        application = mView.findViewById(R.id.dashboard_viewApply);
        process = mView.findViewById(R.id.dashboard_viewProcessing);
        driver = mView.findViewById(R.id.dashboard_viewDriver);

        balance.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverApplication.class)));
        topUp.setOnClickListener(view -> startActivity(new Intent(view.getContext(), TopUp.class)));
        book.setOnClickListener(view -> startActivity(new Intent(view.getContext(), BookScan.class)));
        apply.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverApplication.class)));
        getStarted.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverGetStarted.class)));
        update.setOnClickListener(view -> startActivity(new Intent(view.getContext(), DriverApplication.class).putExtra("mode", 1)));

        Toast.makeText(this.getActivity(), "", Toast.LENGTH_LONG);
        uid = sharedPreferences.getString("uid", "");
        fetchAllData();

        return mView;
    }

    private void fetchAllData() {
        //get balance
        RequestBody formBody = new FormBody.Builder()
                .add("uid", uid)
                .build();

        Request request = new Request.Builder()
                .url("http://www.codexmeraki.ga/android/fetchBalance.php")
                .post(formBody)
                .build();

        thread = new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                JSONObject dataBal = new JSONObject(response.body().string());
                Log.d("Keys", String.valueOf(dataBal.keys()));

                if (dataBal.has("balance")) {
                    this.getActivity().runOnUiThread(() -> {
                        try {
                            Locale locale = new Locale("en", "PH");
                            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

                            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormatter).getDecimalFormatSymbols();
                            decimalFormatSymbols.setCurrencySymbol("");
                            ((DecimalFormat) currencyFormatter).setDecimalFormatSymbols(decimalFormatSymbols);

                            Log.d("RESTBalance", dataBal.getString("balance"));
                            balText.setText(currencyFormatter.format(Double.valueOf(dataBal.getString("balance"))).trim());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                } else {
                    msg = dataBal.has("error") ? dataBal.get("error").toString() : "Something went wrong";
                    Log.d("RESTBalance", msg);
                    popUp.setText(msg);
                    popUp.show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();

        //get history
        formBody = new FormBody.Builder()
                .add("uid", uid)
                .build();

        Request requestHistory = new Request.Builder()
                .url("http://www.codexmeraki.ga/android/fetchHistory.php")
                .post(formBody)
                .build();

        threadHistory = new Thread(() -> {
            try {
                Response responseHis = client.newCall(requestHistory).execute();
                JSONObject dataHistory = new JSONObject(responseHis.body().string());
                Log.d("RESTHistory", String.valueOf(dataHistory.keys()));
                if (dataHistory.has("data")) {
//                        Do RV stuff NOTE: LIMIT TO ONLY 5 ENTRIES TY
                    TreeMap<String, HashMap<String, String>> data = new TreeMap<>(Collections.reverseOrder());
                    for (Iterator<String> it = ((JSONObject) dataHistory.get("data")).keys(); it.hasNext(); ) {
                        String key = it.next();
                        if (data.size() == 5) break;
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

                    this.getActivity().runOnUiThread(() -> {
                        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
                        llm.setOrientation(LinearLayoutManager.VERTICAL);
                        history.setLayoutManager(llm);

                        adapter = new HistoryAdapter(this.getActivity(), data, uid);
                        history.setAdapter(adapter);
                        adapter.setClickListener((view, position) -> {
                            Log.d("Dashboard", view.getTag().toString());
                            LayoutInflater detailsInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View detailsView = detailsInflator.inflate(R.layout.item_history_details, null, false);

                            TextView origin = detailsView.findViewById(R.id.hisDet_txtOrigin),
                                    destination = detailsView.findViewById(R.id.hisDet_txtDestination),
                                    distance = detailsView.findViewById(R.id.hisDet_txtDistance),
                                    price = detailsView.findViewById(R.id.hisDet_txtPrice),
                                    date = detailsView.findViewById(R.id.hisDet_txtDate),
                                    dname = detailsView.findViewById(R.id.hisDet_txtDName),
                                    pname = detailsView.findViewById(R.id.hisDet_txtPName),
                                    plate = detailsView.findViewById(R.id.hisDet_txtDPlate);

                            FormBody fb = new FormBody.Builder()
                                    .add("id", view.getTag().toString()).build();

                            Request req = new Request.Builder()
                                    .url("http://codexmeraki.ga/android/getHistoryDetails.php")
                                    .post(fb)
                                    .build();

                            new Thread(() -> {
                                try {
                                    Response res = client.newCall(req).execute();
                                    String strResponse = res.body().string();
                                    JSONObject json = new JSONObject(strResponse);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (json.has("data")) {
                                                    JSONArray innerData = (JSONArray) json.get("data");
                                                    origin.setText(innerData.getString(ORIGIN));
                                                    destination.setText(innerData.getString(DESTINATION));
                                                    distance.setText(innerData.getString(DISTANCE));
                                                    price.setText(innerData.getString(PRICE));
                                                    date.setText(innerData.getString(TIMESTAMP));
                                                    dname.setText(innerData.getString(DRIVER_FIRSTNAME) + " "
                                                            + innerData.get(DRIVER_MIDDLENAME) + " "
                                                            + innerData.get(DRIVER_LASTNAME));
                                                    pname.setText(innerData.getString(PASSENGER_FIRSTNAME) + " "
                                                            + innerData.get(PASSENGER_MIDDLENAME) + " "
                                                            + innerData.get(PASSENGER_LASTNAME));
                                                    plate.setText(innerData.getString(DRIVER_PLATE_NUMBER));

                                                    new AlertDialog.Builder(view.getContext())
                                                            .setView(detailsView)
                                                            .setTitle("Entry Details")
                                                            .setPositiveButton("Ok", (dialog, i) -> dialog.cancel()).show();
                                                } else {
                                                    Toast.makeText(getActivity(), "An error has occurred", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        });
                    });
                } else {
                    msg = dataHistory.has("error") ? dataHistory.get("error").toString() : "Something went wrong";
                    Log.d("REST", msg);
                    popUp.setText(msg);
                    popUp.show();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        });
        threadHistory.start();

        //set bottom view
        switch (sharedPreferences.getString("type", "")) {
            case "0":
                application.setVisibility(View.VISIBLE);
                break;
            case "1":
                process.setVisibility(View.VISIBLE);
                break;
            case "2":
                driver.setVisibility(View.VISIBLE);
                break;
            default:
                Toast.makeText(this.getActivity(), "An error has occurred. Please log in again.", Toast.LENGTH_SHORT).show();
                sharedPreferences.edit().clear().apply();
        }
    }

    @Override
    public void onPause() {
        if(thread.isAlive()) thread.interrupt();
        if(threadHistory.isAlive()) threadHistory.interrupt();
        super.onPause();
    }
}