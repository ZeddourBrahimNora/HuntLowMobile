package com.example.huntlow;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUsername;
    private long openCount = 0;
    private boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("username")) {
            currentUsername = getIntent().getStringExtra("username");
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState != null) {
            // Restore the saved state
            openCount = savedInstanceState.getLong("openCount");
            isInitialized = savedInstanceState.getBoolean("isInitialized");
        }

        if (!isInitialized) {
            isInitialized = true;
            incrementAppOpenCount();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                    new SearchFragment()).commit();
            bottomNav.setSelectedItemId(R.id.navigation_search);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("openCount", openCount);
        outState.putBoolean("isInitialized", isInitialized);
    }

    private void incrementAppOpenCount() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String lastOpenedDate = prefs.getString("last_opened_date", "");

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_opened_date", currentDate);
        editor.apply();

        openCount++;

        String username = getCurrentUsername();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("appOpens").child(username);
        DatabaseReference dateRef = userRef.child(currentDate);

        dateRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                long count = task.getResult().getValue(Long.class);
                dateRef.setValue(count + 1);
            } else {
                dateRef.setValue(1);
            }
        });
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_search) {
                    selectedFragment = new SearchFragment();
                } else if (id == R.id.navigation_groups) {
                    selectedFragment = new GroupsFragment();
                } else if (id == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                            selectedFragment).commit();
                }

                return true;
            };
}
