package com.example.henrytran.whattowatch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by henrytran on 16-07-02.
 */
public class ImageAdapter extends ArrayAdapter<Movie> {

    public ImageAdapter(Activity context, List<Movie> movieList) {
        super(context, 0, movieList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        //If this is a new view object then inflate the layout
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movie_item,parent,false);
        }

        ImageView imageView = (ImageView) convertView;

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
