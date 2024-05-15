package com.example.huntlow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String groupId);
    }

    public GroupAdapter(List<Group> groupList, OnItemClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupNameTextView.setText(group.getName());
        holder.targetProductTextView.setText(group.getTargetProduct());
        holder.joinButton.setOnClickListener(v -> listener.onItemClick(group.getId()));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setGroups(List<Group> groups) {
        this.groupList = groups;
        notifyDataSetChanged();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView groupNameTextView;
        public TextView targetProductTextView;
        public Button joinButton;

        public GroupViewHolder(View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameTextView);
            targetProductTextView = itemView.findViewById(R.id.targetProductTextView);
            joinButton = itemView.findViewById(R.id.joinButton);
        }
    }
}
