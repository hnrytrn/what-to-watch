package com.example.henrytran.whattowatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by henrytran on 16-07-02.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {

    private Context mContext;

    public ImageAdapter(Context context, List<Movie> movieList) {
        super(context, 0, movieList);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        //construct the url to the movie poster
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w185";

        String posterURL = baseUrl + posterSize + movie.posterPath;

        Picasso.with(this.getContext())
                .load(posterURL)
                //.placeholder(R.raw.placeholder)
                //.error(R.raw.error)
                .noFade().resize(150, 150)
                .centerCrop()
                .into(imageView);

        return imageView;
    }
}
