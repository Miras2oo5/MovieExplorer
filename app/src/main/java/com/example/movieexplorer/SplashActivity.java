package com.example.movieexplorer;
import com.example.movieexplorer.utils.SharedPrefManager;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Очищаем сессию при каждом запуске
        SharedPrefManager.getInstance(this).clearSession();

        // Перенаправляем на экран входа
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}