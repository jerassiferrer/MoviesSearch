package com.mobile.jera.moviessearch.ui;

/**
 * Created by jera on 6/1/17.
 */
import android.app.ActionBar;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mobile.jera.moviessearch.R;
import com.mobile.jera.moviessearch.adapters.RVReviewsAdapter;
import com.mobile.jera.moviessearch.entities.PageReviews;
import com.mobile.jera.moviessearch.entities.PageVideos;
import com.mobile.jera.moviessearch.entities.Result;
import com.mobile.jera.moviessearch.entities.Review;
import com.mobile.jera.moviessearch.entities.Video;
import com.mobile.jera.moviessearch.retrofit.RetrofitInterface;
import com.orm.SugarRecord;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.bumptech.glide.Glide;

public class DetailActivity extends AppCompatActivity  {

    private static final int RECOVERY_REQUEST = 1;

    TextView detailTitle;
    TextView detailOriginalTitle;
    TextView detailOverview;
    TextView detailVoteAverage;
    TextView detailReleaseDate;
    ImageView detailPoster;
    String API_KEY;
    String API_IMAGE_URL;
    String language;
    ActionBar actionBar;
    String result_id;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    RVReviewsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_detail);
        actionBar = getActionBar();
        API_KEY = getString(R.string.API_KEY);
        API_IMAGE_URL = getString(R.string.API_IMAGES_URL);
        language = getString(R.string.language);
        detailTitle = (TextView) findViewById(R.id.detail_title);
        detailOriginalTitle = (TextView) findViewById(R.id.detail_original_title);
        detailOverview = (TextView) findViewById(R.id.detail_overview);
        detailVoteAverage = (TextView) findViewById(R.id.detail_vote_average);
        detailReleaseDate = (TextView) findViewById(R.id.detail_release_date);
        detailPoster = (ImageView) findViewById(R.id.detail_poster);

        if (getIntent().hasExtra("result_id")) {
            initDetails();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recycler_list_reviews);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(3000);
        itemAnimator.setRemoveDuration(3000);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void initDetails() {
        result_id = getIntent().getExtras().getString("result_id");
        try {
            List<Result> results = SugarRecord.find(Result.class, "id = ?", result_id);
            if (results.size() > 0) {
                displayDetails(results.get(0));
            }
        } catch (Exception e){
            Log.e("MYTAG", "Error getting results "+e.getMessage());
            e.printStackTrace();
        }
    }

    public void displayDetails(Result result) {
        detailTitle.setText(result.getTitle());
        detailOriginalTitle.setText(result.getOriginalTitle());
        detailOverview.setText(result.getOverview());
        detailVoteAverage.setText(result.getVoteAverage().toString());
        detailReleaseDate.setText(result.getReleaseDate());
        loadYoutubeVideo(result.getId().toString());
        Glide.with(this)
                .load(API_IMAGE_URL + result.getPosterPath())
                .into(detailPoster);
    }

    public void loadYoutubeVideo(String id) {
        Log.i("MYTAG", "Loading media for: " + id);
        FetchMovieMedia mediatask = new FetchMovieMedia();
        mediatask.execute(id, API_KEY, language);
        FetchMovieReviews reviewstask = new FetchMovieReviews();
        reviewstask.execute(id, API_KEY, language);
    }

    public void setupVideo(String v) {
        Log.i("MYTAG", "Loading video: " + v);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    private class FetchMovieMedia extends AsyncTask<String, Integer, List<Video>> {

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<Video> resultstask) {
            super.onPostExecute(resultstask);
            try {
                if (resultstask==null) {
                    Toast.makeText(DetailActivity.this, "No response from media", Toast.LENGTH_SHORT).show();
                } else {
                    if (resultstask.size()>0) {
                        Video ytvideo = resultstask.get(resultstask.size()-1);
                        setupVideo(ytvideo.getKey().toString());
                    } else {
                        Log.i("MYTAG", "No media found");
                    }
                }
            } catch (Exception e) {

            }
        }

        @Override
        protected List<Video> doInBackground(String... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.API_JSON_URL))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface rfInterface = retrofit.create(RetrofitInterface.class);
            Call<PageVideos> request = rfInterface.fetchMovieTrailers(
                    params[0],
                    params[1],
                    params[2]);

            List<Video> resultsasync = new ArrayList<>();
            PageVideos pages = null;
            try {
                pages = request.execute().body();
                resultsasync = pages.getResults();
            } catch (Exception e) {
                Log.e("MYTAG", "Error getting media "+e.getMessage());
                e.printStackTrace();
            }
            return resultsasync;
        }
    }

    private class FetchMovieReviews extends AsyncTask<String, Integer, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... params) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getResources().getString(R.string.API_JSON_URL))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            RetrofitInterface rfInterface = retrofit.create(RetrofitInterface.class);
            Call<PageReviews> request = rfInterface.fetchMovieReviews(
                    params[0],
                    params[1],
                    params[2]);

            List<Review> resultsasync = new ArrayList<>();
            PageReviews pages = null;
            try {
                pages = request.execute().body();
                resultsasync = pages.getResults();
            } catch (Exception e) {
                Log.e("MYTAG", "Error getting reviews "+e.getMessage());
                e.printStackTrace();
            }
            return resultsasync;
        }

        @Override
        protected void onPostExecute(List<Review> resultstask) {
            super.onPostExecute(resultstask);
            try {
                if (resultstask==null) {
                    Toast.makeText(DetailActivity.this, "No response from reviews", Toast.LENGTH_SHORT).show();
                } else {
                    if (resultstask.size()>0) {
                        for (Review rev:resultstask) {
                            adapter = new RVReviewsAdapter(resultstask);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.i("MYTAG", "No reviews found");
                    }
                }
            } catch (Exception e) {

            }
        }

    }
}
