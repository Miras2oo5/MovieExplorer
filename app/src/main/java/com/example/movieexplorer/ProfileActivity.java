package com.example.movieexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieexplorer.model.User;
import com.example.movieexplorer.utils.SharedPrefManager;

public class ProfileActivity extends AppCompatActivity {
    private TextView textViewUsername, textViewEmail;
    private Button buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Проверка авторизации
        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        textViewUsername = findViewById(R.id.textViewUsername);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Получаем данные пользователя
        User user = SharedPrefManager.getInstance(this).getUser();

        // Отображаем данные
        textViewUsername.setText("Имя пользователя: " + user.getUsername());
        textViewEmail.setText("Email: " + user.getEmail());

        buttonLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(this).logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}