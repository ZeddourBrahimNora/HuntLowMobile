package com.example.huntlow;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button buttonAddMarker;
    private String groupId;
    private String targetProduct;

    private static final LatLng BELGIUM_COORDINATES = new LatLng(50.8503, 4.3517); // Coordinates for Belgium

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        buttonAddMarker = view.findViewById(R.id.buttonAddMarker);
        buttonAddMarker.setOnClickListener(v -> addMarker());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (getArguments() != null) {
            groupId = getArguments().getString("groupId");
            targetProduct = getArguments().getString("targetProduct");
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Center the map on Belgium
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BELGIUM_COORDINATES, 8));

        // Optionally, zoom to the user's current location if available
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    }
                });
    }

    private void addMarker() {
        if (mMap != null) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            LatLng markerLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(markerLocation).title("Produit trouvé ici"));
                            saveMarkerToDatabase(markerLocation);
                        } else {
                            Toast.makeText(getActivity(), "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void saveMarkerToDatabase(LatLng location) {
        DatabaseReference markersRef = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("markers");
        String markerId = markersRef.push().getKey();
        MarkerData markerData = new MarkerData(markerId, location.latitude, location.longitude);
        markersRef.child(markerId).setValue(markerData)
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity(), "Marqueur ajouté", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Erreur lors de l'ajout du marqueur", Toast.LENGTH_SHORT).show());
    }

    public static class MarkerData {
        public String id;
        public double latitude;
        public double longitude;

        public MarkerData() {
            // Default constructor required for calls to DataSnapshot.getValue(MarkerData.class)
        }

        public MarkerData(String id, double latitude, double longitude) {
            this.id = id;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}
