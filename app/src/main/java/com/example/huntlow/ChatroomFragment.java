package com.example.huntlow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSendMessage;
    private TextView textViewWelcome;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef;
    private String groupId;

    private Button buttonOpenMap;

    private String targetProduct;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSendMessage = view.findViewById(R.id.buttonSendMessage);
        textViewWelcome = view.findViewById(R.id.textViewWelcome);
        buttonOpenMap = view.findViewById(R.id.buttonOpenMap);


        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        // Récupérer le groupId depuis les arguments
        if (getArguments() != null) {
            groupId = getArguments().getString("groupId");
            targetProduct = getArguments().getString("targetProduct");
        } else {
            Toast.makeText(getContext(), "Group ID is null", Toast.LENGTH_SHORT).show();
            return view;
        }

        // Référence de la chatroom spécifique au groupe
        messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(groupId);

        // Charger les messages
        loadMessages();

        // Charger les informations du groupe
        loadGroupInfo();

        buttonSendMessage.setOnClickListener(v -> sendMessage());

        buttonOpenMap.setOnClickListener(v -> openMapFragment());


        return view;
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String currentUsername = ((MainActivity) getActivity()).getCurrentUsername();
            Message message = new Message(currentUsername, messageText);

            messagesRef.push().setValue(message).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    editTextMessage.setText("");
                    Toast.makeText(getContext(), "Message sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void loadGroupInfo() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupId);
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Group group = snapshot.getValue(Group.class);
                    if (group != null) {
                        textViewWelcome.setText("Bienvenue dans le groupe: " + group.getGroupName() + "\nObjectif: Trouver le produit '" + group.getTargetProduct() + "' au prix le plus bas possible !");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load group info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMapFragment() {
        MapFragment mapFragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString("groupId", groupId);
        args.putString("targetProduct", targetProduct);
        mapFragment.setArguments(args);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, mapFragment)
                .addToBackStack(null)
                .commit();
    }
}
