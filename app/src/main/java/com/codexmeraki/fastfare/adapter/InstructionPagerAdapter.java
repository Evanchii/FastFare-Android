package com.codexmeraki.fastfare.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.codexmeraki.fastfare.R;
import com.codexmeraki.fastfare.tutorial.ScreenItem;

import java.util.List;

public class InstructionPagerAdapter extends PagerAdapter {
    Context mContext ;
    List<ScreenItem> mListScreen;

    public InstructionPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutScreen = inflater.inflate(R.layout.item_instructions,null);

        ImageView imgSlide = layoutScreen.findViewById(R.id.intro_img);
        TextView title = layoutScreen.findViewById(R.id.intro_title);
        TextView description = layoutScreen.findViewById(R.id.intro_description);
        Button contact = layoutScreen.findViewById(R.id.intro_contact);
        contact.setOnClickListener(v -> {
            Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","tneck2020@gmail.com", null));
            email.putExtra(Intent.EXTRA_SUBJECT, "Support");
            email.putExtra(Intent.EXTRA_TEXT, "");
            mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
        });

        title.setText(mListScreen.get(position).getTitle());
        if(mListScreen.get(position).getDescription().equals("END-CONTACT")) {
            contact.setVisibility(View.VISIBLE);
            description.setVisibility(View.GONE);
        }
        else
            description.setText(mListScreen.get(position).getDescription());
        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());

        container.addView(layoutScreen);

        return layoutScreen;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((View)object);

    }
}
