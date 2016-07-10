package com.example.henrytran.whattowatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_container, new DetailActivityFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    public static class DetailActivityFragment extends Fragment {

        private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

        private Movie mMovie;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("movieTag")) {
                mMovie = intent.getParcelableExtra("movieTag");

                //set movie details in the fragment
                ((TextView) rootView.findViewById(R.id.detail_title))
                        .setText(mMovie.title);
                ((TextView) rootView.findViewById(R.id.detail_synopsis))
                        .setText(mMovie.synopsis);
                ((TextView) rootView.findViewById(R.id.detail_rating))
                        .setText(String.valueOf(mMovie.rating));
                ((TextView) rootView.findViewById(R.id.detail_release))
                        .setText(mMovie.releaseDate);

                String baseUrl = "http://image.tmdb.org/t/p/";
                String posterSize = "w185";
                String posterURL = baseUrl + posterSize + mMovie.posterPath;

                Picasso.with(getContext())
                        .load(posterURL)
                        .noFade().resize(200, 300)
                        .into(((ImageView) rootView.findViewById(R.id.detail_image)));
            }
            return rootView;
        }
    }
}
