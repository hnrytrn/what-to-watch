package com.example.henrytran.whattowatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by henrytran on 16-07-05.
 */
public class Movie implements Parcelable{
    String title;
    String synopsis;
    double rating;
    String releaseDate;
    String posterPath;

    public Movie (String title, String synopsis, double rating, String releaseDate, String posterPath) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }


    protected Movie(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.title = data[0];
        this.synopsis = data[1];
        this.rating = Double.parseDouble(data[2]);
        this.releaseDate = data[3];
        this.posterPath = data[4];
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
            this.title,
            this.synopsis,
            String.valueOf(this.rating),
            this.releaseDate,
            this.posterPath});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
