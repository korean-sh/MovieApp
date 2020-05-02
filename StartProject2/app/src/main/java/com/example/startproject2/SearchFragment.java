package com.example.startproject2;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    MovieAdapter adapter;
    RecyclerView recyclerView;
    static RequestQueue requestQueue;

    String clientId = "naver_id";
    String clientSecret = "naver_password";
    String uriString = "content://com.example.startproject2/movie";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search, container, false);
        adapter = new MovieAdapter();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        queryPerson();

        final SearchView searchView = rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.clearItems();
                makeRequest(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        return rootView;
    }

    private void makeRequest(String query) {
        String apiURL = "https://openapi.naver.com/v1/search/movie.json?query="+query;
        StringRequest request = new StringRequest(Request.Method. GET , apiURL, new Response.Listener<String>() {
            @Override public void onResponse(String response) {
                processResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("makeRequest", error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-Naver-Client-Id",clientId);
                params.put("X-Naver-Client-Secret",clientSecret);
                return params;
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void processResponse(String response){
        Gson gson = new Gson();
        MovieList movieList = gson.fromJson(response, MovieList.class);
        clearMovie(); //초기화
        insertMovie(movieList); //내용제공자 insert
        for(int i = 0; i < movieList.items.size(); i++){
            Movie movie = movieList.items.get(i);
            adapter.addItem(movie);
        }
        adapter.notifyDataSetChanged();
    }

    private void insertMovie(MovieList movieList) {

        Uri uri = new Uri.Builder().build().parse(uriString);
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);

        if(movieList.items.size() != 0) {
            for (int i = 0; i < movieList.items.size(); i++) {
                Movie movie = movieList.items.get(i);
                ContentValues values = new ContentValues();
                values.put("title", movie.title);
                values.put("link", movie.link);
                values.put("image", movie.image);
                values.put("pubDate", movie.pubDate);
                values.put("director", movie.director);
                values.put("actor", movie.actor);
                values.put("rating", movie.userRating);
                uri = getActivity().getContentResolver().insert(uri, values);
            }
        }
    }

    private void queryPerson() {
        Log.d("PPAP","호출");
        Uri uri = new Uri.Builder().build().parse(uriString);
        String[] columns = new String[] {"title", "link", "image", "pubDate","director","actor","rating"};
        Cursor cursor = getActivity().getContentResolver().query(uri, columns, null, null, "name ASC");
        int index = 0;
        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndex(columns[0]));
            String link = cursor.getString(cursor.getColumnIndex(columns[1]));
            String image = cursor.getString(cursor.getColumnIndex(columns[2]));
            String pubDate = cursor.getString(cursor.getColumnIndex(columns[3]));
            String director = cursor.getString(cursor.getColumnIndex(columns[4]));
            String actor = cursor.getString(cursor.getColumnIndex(columns[5]));
            String rating = cursor.getString(cursor.getColumnIndex(columns[6]));

            Movie movie = new Movie(title, link, image, pubDate, director, actor, rating);
            adapter.addItem(movie);
        }
    }

    private void clearMovie() { //기생충
        Log.d("PPAP","clearMovie");
        Uri uri = new Uri.Builder().build().parse(uriString);
        int count = getActivity().getContentResolver().delete(uri, null, null);
    }
} //END
