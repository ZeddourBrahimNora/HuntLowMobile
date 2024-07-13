package com.example.huntlow;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

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
    private ProfileViewModel viewModel;
    private boolean isAppOpened = false;
    private boolean isChangingConfigurations = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        auth = FirebaseAuth.getInstance();

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        if (getIntent().hasExtra("username")) {
            currentUsername = getIntent().getStringExtra("username");
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                    new SearchFragment()).commit();
            bottomNav.setSelectedItemId(R.id.navigation_search);
        }

        if (!viewModel.isInitialized()) {
            viewModel.setInitialized(true);
        }
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

    private void incrementAppOpenCount() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String lastOpenedDate = prefs.getString("last_opened_date", "");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Incrémentez le compteur seulement si l'application n'a pas été ouverte aujourd'hui
        if (!currentDate.equals(lastOpenedDate)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_opened_date", currentDate);
            editor.apply();

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
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isChangingConfigurations = true;
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment instanceof ProfileFragment) {
            ((ProfileFragment) currentFragment).onOrientationChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isChangingConfigurations) {
            incrementAppOpenCount();
        }
        isAppOpened = true;
        isChangingConfigurations = false; // Reset the flag after configuration change
    }

    @Override
    protected void onPause() {
        super.onPause();
        isAppOpened = false; // Reset the flag when the activity is paused
    }
}
