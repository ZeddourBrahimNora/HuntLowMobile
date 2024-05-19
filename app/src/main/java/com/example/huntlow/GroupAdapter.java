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
    private OnJoinClickListener onJoinClickListener;

    public interface OnJoinClickListener {
        void onJoinClick(String groupId);
    }

    public GroupAdapter(List<Group> groupList, OnJoinClickListener onJoinClickListener) {
        this.groupList = groupList;
        this.onJoinClickListener = onJoinClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.groupName.setText(group.getGroupName());
        holder.targetProduct.setText(group.getTargetProduct());

        holder.joinButton.setOnClickListener(v -> onJoinClickListener.onJoinClick(group.getGroupId()));
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;
        public TextView targetProduct;
        public Button joinButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupNameTextView);
            targetProduct = itemView.findViewById(R.id.targetProductTextView);
            joinButton = itemView.findViewById(R.id.joinButton);
        }
    }

    public void setGroups(List<Group> groups) {
        this.groupList = groups;
        notifyDataSetChanged();
    }
}
