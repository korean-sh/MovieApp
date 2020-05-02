package com.example.startproject2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView; //기생충

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>{

    ArrayList<Movie> items = new ArrayList<Movie>();
    Handler handler = new Handler();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.movie_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Movie item = items.get(position);
        holder.setItem(item);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.link));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Movie item){
        items.add(item);
    }

    public void setItems(ArrayList<Movie> items){
        this.items = items;
    }

    public Movie getItem(int position) {
        return items.get(position);
    }

    public void clearItems() {
        this.items.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2, textView3, textView4;
        ImageView imageView;
        RatingBar ratingBar;

        public final View layout;

        Bitmap bitmap;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            textView3 = itemView.findViewById(R.id.textView3);
            textView4 = itemView.findViewById(R.id.textView4);
            imageView = itemView.findViewById(R.id.imageView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            layout = itemView;
        }

        public void setItem(final Movie item) {
            //영화 제목
            String movie1 = item.title;
            String movie2 = movie1.replaceAll("<b>","");
            String movie3 = movie2.replaceAll("</b>","");

            //감독 이름
            String director1 = item.director.replace("|",",");
            if(director1.length() > 0){
                if(director1.indexOf(",") == director1.length()-1){
                    director1 = director1.replaceAll(",","");
                }
            }else{
                director1 = "감독정보 없음";
            }

            //배우 이름
            String actor1 = item.actor;
            String actor2 = actor1.replace("|",",");
            String actor3 = "";
            if(actor2.length() > 0){
                actor3 = actor2.substring(0,actor2.length()-1);
            }else{
                actor3 = "출연정보 없음";
            }

            textView.setText(movie3+" ("+item.pubDate+")");
            textView2.setText("감독: " + director1);
            textView3.setText("출연: " + actor3);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(item.image != null){
                            URL url = new URL(item.image);
                            InputStream instream = url.openStream();
                            bitmap = BitmapFactory.decodeStream(instream);
                            handler.post(new Runnable() {//외부쓰레드에서 메인 UI에 접근하기 위해 Handler를 사용
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(bitmap);
                                }
                            });
                        }else{
                            imageView.setImageResource(R.drawable.movie);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

            if(item.userRating != 0) {
                textView4.setText(item.userRating + " ");
                ratingBar.setVisibility(View.VISIBLE);
                ratingBar.setRating(item.userRating / 2);
            } else {
                textView4.setText("평점 없음");
                ratingBar.setVisibility(View.GONE);
            }
        }
    }
}
