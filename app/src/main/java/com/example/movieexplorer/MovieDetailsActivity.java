package com.example.movieexplorer;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieexplorer.api.ApiClient;
import com.example.movieexplorer.api.ApiInterface;
import com.example.movieexplorer.model.Movie;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    private ImageView imageViewPoster;
    private TextView textViewTitle, textViewRating, textViewReleaseDate, textViewOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Инициализация элементов интерфейса
        imageViewPoster = findViewById(R.id.imageViewPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewRating = findViewById(R.id.textViewRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewOverview = findViewById(R.id.textViewOverview);

        // Получаем ID фильма из Intent
        int movieId = getIntent().getIntExtra("movie_id", 0);
        if (movieId == 0) {
            Toast.makeText(this, "Ошибка: фильм не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Загружаем данные о фильме
        loadMovieDetails(movieId);
    }

    private void loadMovieDetails(int movieId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Movie> call = apiService.getMovieDetails(movieId, "ВАШ_API_КЛЮЧ");

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displayMovieDetails(response.body());
                } else {
                    Toast.makeText(MovieDetailsActivity.this,
                            "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this,
                        "Ошибка соединения", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMovieDetails(Movie movie) {
        textViewTitle.setText(movie.getTitle());
        textViewRating.setText(String.format("Рейтинг: %.1f", movie.getVoteAverage()));
        textViewReleaseDate.setText(String.format("Дата выхода: %s", movie.getReleaseDate()));
        textViewOverview.setText(movie.getOverview());

        // Загрузка изображения с помощью Glide
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .into(imageViewPoster);
    }
}