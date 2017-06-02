package com.mobile.jera.moviessearch.retrofit;

import com.mobile.jera.moviessearch.entities.PageResults;
import com.mobile.jera.moviessearch.entities.Result;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    public static void main(String[] args) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitInterface rfInterface = retrofit.create(RetrofitInterface.class);

        // TEST CASE FOR UPCOMING MOVIES
        Call<PageResults> request = rfInterface.upcomingMovies("cac0b89ef7b5aa3a980f240f7c20af68", "1306691868", "1", "en");

        PageResults pages = null;
        try {
            pages = request.execute().body();
            for (Result result : pages.getResults()) {
                System.out.println(result.getId()+" "+result.getTitle());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // TEST CASE FOR POPULAR NOW MOVIES
        /*Call<PageResults> request = rfInterface.mostPopularMovies("cac0b89ef7b5aa3a980f240f7c20af68",
                                            "popularity.desc",
                                            "1",
                                            "pt");

        PageResults pages = null;
        try {
            pages = request.execute().body();
            for (Result result : pages.getResults()) {
                System.out.println(result.getId()+" "+result.getTitle());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }*/

        // TEST CASE FOR MEDIA BY MOVIE ID
        /*Call<PageVideos> request = rfInterface.fetchVideosOfMovie(
                "110415",
                "cac0b89ef7b5aa3a980f240f7c20af68",
                "en");

        PageVideos pages = null;

        try {
            pages = request.execute().body();
            for (Video result : pages.getResults()) {
                System.out.println(result.getSite()+" "+result.getKey());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }*/
    }
}