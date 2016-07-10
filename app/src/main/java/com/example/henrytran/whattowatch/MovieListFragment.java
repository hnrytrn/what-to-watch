package com.example.henrytran.whattowatch;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by henrytran on 16-07-08.
 */
public class MovieListFragment extends Fragment {

    private static ImageAdapter mImageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popular) {
            loadMovies("popular");
            return true;
        } else if (id == R.id.action_rated) {
            loadMovies("top_rated");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movielist, container, false);

        mImageAdapter = new ImageAdapter(getActivity(), new ArrayList<Movie>());

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = mImageAdapter.getItem(position);

                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movieTag", movie);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadMovies("popular");
    }

    private void loadMovies(String type) {
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute(type);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        //parse the movie data from the json string
        private ArrayList<Movie> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            //names of the JSON objects that need to be extracted
            final String MDB_RESULTS = "results";
            final String MDB_BACKDROP = "backdrop_path";
            final String MDB_TITLE = "original_title";
            final String MDB_SYNOPSIS = "overview";
            final String MDB_RELEASE = "release_date";
            final String MDB_RATING = "vote_average";

            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray resultsJson = movieJson.getJSONArray(MDB_RESULTS);

            int moviesLength = resultsJson.length();

            ArrayList<Movie> movieList = new ArrayList<Movie>();
            for (int i = 0; i < moviesLength; i++) {
                JSONObject movieResult = resultsJson.getJSONObject(i);

                String title = movieResult.getString(MDB_TITLE);
                String synopsis = movieResult.getString(MDB_SYNOPSIS);
                double rating = movieResult.getDouble(MDB_RATING);
                String releaseDate = movieResult.getString(MDB_RELEASE);
                String posterPath = movieResult.getString(MDB_BACKDROP);

                //add the movie object to the movieList
                movieList.add(new Movie(title, synopsis, rating, releaseDate, posterPath));
            }
            return movieList;
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection =  null;
            BufferedReader reader = null;

            //The raw JSON response from the movie database
            String moviesJsonStr = null;

            try {
                //construct the URL to fetch the popular movies from the movie db
                String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
                final String API_PARAM = "api_key";
                //append the param for what the user wants to see in the base url
                if (params[0] == "popular"  || params[0] == "top_rated") {
                    MOVIE_BASE_URL += params[0];
                }

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                //create the request to the movie db and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (!movies.isEmpty()) {
                mImageAdapter.clear();

                int moviesSize = movies.size();
                Movie[] movieArr = new Movie[moviesSize];
                movies.toArray(movieArr);

                for(int i=0; i < moviesSize; i++) {
                    mImageAdapter.add(movieArr[i]);
                }
            }
        }
    }
}
