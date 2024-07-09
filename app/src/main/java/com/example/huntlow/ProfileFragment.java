package com.example.huntlow;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    private TextView textViewUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        textViewUsername = view.findViewById(R.id.textViewUsername);

        // Obtenez le nom d'utilisateur actuel depuis l'activité
        String currentUsername = ((MainActivity) getActivity()).getCurrentUsername();
        textViewUsername.setText(currentUsername);

        return view;
    }
}
