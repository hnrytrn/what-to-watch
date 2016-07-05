package com.example.henrytran.whattowatch;

/**
 * Created by henrytran on 16-07-05.
 */
public class Movie {
    String title;
    String synopsis;
    float rating;
    String releaseDate;
    String posterPath;

    public Movie (String title, String synopsis, float rating, String releaseDate, String posterPath) {
        this.title = title;
        this.synopsis = synopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }
}
