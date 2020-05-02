package com.example.startproject2;

public class Movie {
    String title;
    String link;
    String image;
    String subtitle;
    String pubDate;
    String director;
    String actor;
    float userRating;

    //Movie movie = new Movie(title, link, image, pubDate, director, actor, rating);

    public Movie(String title, String link, String image, String pubDate, String director, String actor, String userRating){
        this.title = title;
        this.link = link;
        this.image = image;
        this.director = director;
        this.pubDate = pubDate;
        this.director = director;
        this.actor = actor;
        this.userRating = Float.parseFloat(userRating);
    }
}
