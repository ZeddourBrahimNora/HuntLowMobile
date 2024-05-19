package com.example.huntlow;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatroomFragment extends Fragment {

    private RecyclerView recyclerViewMessages;
    private EditText editTextMessage;
    private Button buttonSendMessage;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference messagesRef;
    private String currentUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatroom, container, false);

        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSendMessage = view.findViewById(R.id.buttonSendMessage);

        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getActivity()));
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);

        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", "Unknown");

        buttonSendMessage.setOnClickListener(v -> sendMessage());

        loadMessages();

        return view;
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String currentUserId = currentUsername;

            Message message = new Message(currentUserId, currentUsername, messageText);
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
}
