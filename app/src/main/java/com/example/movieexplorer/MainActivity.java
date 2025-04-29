package com.example.movieexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieexplorer.adapters.MovieAdapter;
import com.example.movieexplorer.api.ApiClient;
import com.example.movieexplorer.api.ApiInterface;
import com.example.movieexplorer.model.Movie;
import com.example.movieexplorer.model.MovieResponse;
import com.example.movieexplorer.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Проверка авторизации
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initializeUI();
        setupRecyclerView();
        loadPopularMovies();
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movieList);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(movieAdapter);

        movieAdapter.setOnItemClickListener(position -> {
            if (position >= 0 && position < movieList.size()) {
                Movie movie = movieList.get(position);
                if (movie != null) {
                    openMovieDetails(movie.getId());
                } else {
                    showError("Данные фильма не загружены");
                }
            }
        });
    }

    private void loadPopularMovies() {
        progressBar.setVisibility(View.VISIBLE);
        movieList.clear();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiService.getPopularMovies("854c5f73dfc2671b1dea661201afacac", 1);

        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    MovieResponse movieResponse = response.body();
                    if (movieResponse != null && movieResponse.getMovies() != null) {
                        updateMovieList(movieResponse.getMovies());
                    } else {
                        showError("Нет данных о фильмах");
                        Log.e(TAG, "Пустой ответ от API");
                    }
                } else {
                    showError("Ошибка сервера: " + response.code());
                    Log.e(TAG, "API error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showError("Ошибка сети: " + t.getMessage());
                Log.e(TAG, "Network failure", t);
            }
        });
    }

    private void updateMovieList(List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            movieList.addAll(movies);
            movieAdapter.notifyDataSetChanged();
        } else {
            showError("Список фильмов пуст");
        }
    }

    private void openMovieDetails(int movieId) {
        Log.d(TAG, "Opening movie details for ID: " + movieId);
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        intent.putExtra("movie_id", movieId);
        startActivity(intent);
    }

    private void redirectToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        } else if (id == R.id.menu_logout) {
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Подтверждение выхода")
                .setMessage("Вы уверены, что хотите выйти из аккаунта?")
                .setPositiveButton("Выйти", (dialog, which) -> performLogout())
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void performLogout() {
        SharedPrefManager.getInstance(this).logout();
        redirectToLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Освобождение ресурсов при необходимости
    }
}