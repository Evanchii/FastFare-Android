package com.codexmeraki.fastfare.recyclerAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codexmeraki.fastfare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private TreeMap<String, HashMap<String, String>> mData;
    private LayoutInflater mInflator;
    private ItemClickListener mClickListener;
    private String mUid;

    public HistoryAdapter(Context context, TreeMap<String, HashMap<String, String>> data, String uid) {
        this.mInflator = LayoutInflater.from(context);
        this.mData = data;
        this.mUid = uid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String key = (String) (mData.keySet().toArray())[position];

        holder.cardView.setTag(key);
        holder.amount.setText(mData.get(key).get("amount") + " >");
        holder.time.setText(mData.get(key).get("timestamp"));
        holder.details.setText(mData.get(key).get("origin") + " -> " + mData.get(key).get("destination"));
        holder.icon.setImageResource( mUid.equals(mData.get(key).get("passenger_uid"))
                ? R.drawable.ic_pin_a : R.drawable.ic_pin_b);
    }

    @Override
    public int getItemCount() {
        Log.d("LocAdapter>", mData.keySet().toString());
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView time, details, amount;
        CardView cardView;
        ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.itemHistory_txtTime);
            details = itemView.findViewById(R.id.itemHistory_txtDetails);
            icon = itemView.findViewById(R.id.itemHistory_imgIcon);
            cardView = itemView.findViewById(R.id.itemHistory_card);
            cardView.setOnClickListener(this);
            amount = itemView.findViewById(R.id.itemHistory_txtPrice);
//            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
