package com.example.henrytran.whattowatch;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

public class MainActivity extends Activity {

    private static ImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mImageAdapter = new ImageAdapter(this, new ArrayList<Movie>());

        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    /*    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        FetchMovieTask fetchMovieTask = new FetchMovieTask();
        fetchMovieTask.execute();
    }

    public class FetchMovieTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

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
        protected ArrayList<Movie> doInBackground(Void... params) {
            HttpURLConnection urlConnection =  null;
            BufferedReader reader = null;

            //The raw JSON response from the movie database
            String moviesJsonStr = null;

            try {
                //construct the URL to fetch the popular movies from the movie db
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular";
                final String API_PARAM = "api_key";

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
