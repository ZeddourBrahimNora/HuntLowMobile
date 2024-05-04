package com.example.huntlow;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Pour charger le premier fragment par défaut (Recherche de produits)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                    new SearchFragment()).commit();
            bottomNav.setSelectedItemId(R.id.navigation_search); // Mettez en surbrillance l'icône de recherche par défaut
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_search) {
                    selectedFragment = new SearchFragment();
                } else if (id == R.id.navigation_map) {
                    selectedFragment = new MapFragment();
                } else if (id == R.id.navigation_groups) {
                    selectedFragment = new GroupsFragment();
                }


                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,
                            selectedFragment).commit();
                }

                return true;
            };
}