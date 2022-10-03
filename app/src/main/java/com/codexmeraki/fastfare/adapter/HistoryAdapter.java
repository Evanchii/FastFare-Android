package com.codexmeraki.fastfare.adapter;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.codexmeraki.fastfare.R;

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

        if(key.equals("0")) {
            holder.cardView.setOnClickListener(null);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.details.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

            holder.details.setLayoutParams(params);
            holder.details.setText("No data found..");
            holder.details.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            holder.details.setPadding(16,8,16,8);
            holder.details.setTypeface(null, Typeface.BOLD);
            holder.details.setTextSize(20);

            holder.amount.setVisibility(GONE);
            holder.time.setVisibility(GONE);
            holder.icon.setVisibility(GONE);
        } else {
            holder.cardView.setTag(key);
            holder.amount.setText(mData.get(key).get("amount") + " >");
            holder.time.setText(mData.get(key).get("timestamp"));
            holder.details.setText(mData.get(key).get("origin") + " -> " + mData.get(key).get("destination"));
            holder.icon.setImageResource( mUid.equals(mData.get(key).get("passenger_uid"))
                    ? R.drawable.ic_pin_a : R.drawable.ic_pin_b);
        }
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
