package com.example.huntlow;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private String currentUsername;

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                    new SearchFragment()).commit();
            bottomNav.setSelectedItemId(R.id.navigation_search);
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

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment instanceof ProfileFragment) {
            ((ProfileFragment) currentFragment).onOrientationChanged();
        }
    }
}
