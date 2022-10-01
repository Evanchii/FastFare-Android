package com.codexmeraki.fastfare.recyclerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codexmeraki.fastfare.R;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeMap;

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {
    private TreeMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflator;
    //    private ItemClickListener mClickListener;
    private String mUid;

    public BalanceAdapter(Context context, TreeMap<String, HashMap<String, String>> data, String uid) {
        this.mInflator = LayoutInflater.from(context);
        this.mData = data;
        this.mUid = uid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_balance, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = (String) (mData.keySet().toArray())[position];

        holder.cardView.setTag(key);

        Locale locale = new Locale("en", "PH");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);

        holder.amount.setText(currencyFormatter.format(Double.valueOf(mData.get(key).get("amount"))));
        holder.time.setText(mData.get(key).get("timestamp"));
        if(mData.get(key).get("type").equals("0")) {
            if(mData.get(key).get("sender").equals(mUid)) {
                //minus
                holder.icon.setImageResource(R.drawable.ic_money_minus);
            } else {
                //add
                holder.icon.setImageResource(R.drawable.ic_money_add);
            }
        } else if(mData.get(key).get("type").equals("1")) {
            holder.icon.setImageResource(R.drawable.ic_shop);
        }

        String deets = "From: " + mData.get(key).get("senName")
                + "\nTo: " + mData.get(key).get("recName");
        holder.details.setText(deets);

    }

    @Override
    public int getItemCount() {
        Log.d("LocAdapter>", mData.keySet().toString());
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView time, details, amount;
        CardView cardView;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.itemHistory_txtTime);
            details = itemView.findViewById(R.id.itemHistory_txtDetails);
            icon = itemView.findViewById(R.id.itemHistory_imgIcon);
            cardView = itemView.findViewById(R.id.itemHistory_card);
            amount = itemView.findViewById(R.id.itemHistory_txtPrice);
//            cardView.setOnClickListener(this);
        }
    }
}
