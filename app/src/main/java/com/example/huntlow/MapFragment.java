package com.example.huntlow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String groupId;
    private TextView textViewBanner;
    private static final LatLng BELGIUM_COORDINATES = new LatLng(50.8503, 4.3517); // Coordinates for Belgium

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        textViewBanner = view.findViewById(R.id.textViewBanner);

        if (getArguments() != null) {
            groupId = getArguments().getString("groupId");
            loadGroupInfo();
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Center the map on Belgium
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BELGIUM_COORDINATES, 8));

        // Load existing markers from Firebase
        loadMarkers();

        // Set a map click listener
        mMap.setOnMapClickListener(latLng -> addMarker(latLng));
    }

    private void loadGroupInfo() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupId);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null) {
                        String bannerText = "Marquez l'emplacement du produit '" + group.getTargetProduct() + "' le moins cher que vous avez trouvé ici !";
                        textViewBanner.setText(bannerText);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Erreur lors du chargement des informations du groupe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMarkers() {
        DatabaseReference markersRef = FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("markers");
        markersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMap.clear(); // Clear the map before adding new markers
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MarkerData markerData = snapshot.getValue(MarkerData.class);
                    if (markerData != null) {
                        LatLng position = new LatLng(markerData.latitude, markerData.longitude);
                        mMap.addMarker(new MarkerOptions().position(position).title("Produit trouvé ici"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Erreur lors du chargement des marqueurs", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMarker(LatLng markerLocation) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(markerLocation).title("Produit trouvé ici"));
            saveMarkerToDatabase(markerLocation);
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
