package com.example.movieexplorer.model;

import com.google.gson.annotations.SerializedName;

public class Movie {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("overview")
    private String overview;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("release_date")
    private String releaseDate;

    // Геттеры
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterPath() { return posterPath; }
    public String getOverview() { return overview; }
    public double getVoteAverage() { return voteAverage; }
    public String getReleaseDate() { return releaseDate; }
}