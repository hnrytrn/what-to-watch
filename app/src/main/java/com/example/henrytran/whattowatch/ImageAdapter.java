package com.example.henrytran.whattowatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by henrytran on 16-07-02.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        //check to see if we have a view
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(MainActivity.this)
                .load(mThumbIds[position])
                .placeholder(R.raw.placeholder)
                .error(R.raw.error)
                .noFade().resize(150, 150)
                .centerCrop()
                .into(imageView);
        return imageView;
    }
}
