package com.example.huntlow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupsFragment extends Fragment {

    private EditText groupNameEditText;
    private EditText targetProductEditText;
    private Button createGroupButton;
    private RecyclerView groupsRecyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> groupList;

    private DatabaseReference db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        groupNameEditText = view.findViewById(R.id.groupNameEditText);
        targetProductEditText = view.findViewById(R.id.targetProductEditText);
        createGroupButton = view.findViewById(R.id.createGroupButton);
        groupsRecyclerView = view.findViewById(R.id.groupsRecyclerView);

        db = FirebaseDatabase.getInstance().getReference();

        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(groupList, this::joinGroup);
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        groupsRecyclerView.setAdapter(groupAdapter);

        createGroupButton.setOnClickListener(v -> createGroup());

        loadGroups();

        return view;
    }

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        String targetProduct = targetProductEditText.getText().toString().trim();

        if (groupName.isEmpty() || targetProduct.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String groupId = db.child("groups").push().getKey();

        Group newGroup = new Group(groupId, groupName, targetProduct, new ArrayList<String>());

        db.child("groups").child(groupId).setValue(newGroup)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Group created successfully", Toast.LENGTH_SHORT).show();
                    groupNameEditText.setText("");
                    targetProductEditText.setText("");
                    loadGroups();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error creating group", Toast.LENGTH_SHORT).show());
    }

    private void loadGroups() {
        db.child("groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        Group group = snapshot.getValue(Group.class);
                        if (group != null) {
                            groupList.add(group);
                        } else {
                            GenericTypeIndicator<HashMap<String, Object>> t = new GenericTypeIndicator<HashMap<String, Object>>() {};
                            HashMap<String, Object> map = snapshot.getValue(t);
                            if (map != null) {
                                String id = (String) map.get("id");
                                String name = (String) map.get("name");
                                String targetProduct = (String) map.get("targetProduct");
                                List<String> members = new ArrayList<>();
                                if (map.get("members") instanceof List) {
                                    members = (List<String>) map.get("members");
                                }
                                Group newGroup = new Group(id, name, targetProduct, members);
                                groupList.add(newGroup);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                groupAdapter.setGroups(groupList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error loading groups", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void joinGroup(String groupId) {
        ChatroomFragment chatroomFragment = new ChatroomFragment();
        Bundle args = new Bundle();
        args.putString("groupId", groupId);
        chatroomFragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, chatroomFragment)
                .addToBackStack(null)
                .commit();
    }
}
