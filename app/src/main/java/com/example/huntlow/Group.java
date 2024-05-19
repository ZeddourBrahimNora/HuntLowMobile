package com.example.huntlow;

import java.util.List;

public class Group {
    private String id;
    private String name;
    private String targetProduct;
    private List<String> members;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String id, String name, String targetProduct, List<String> members) {
        this.id = id;
        this.name = name;
        this.targetProduct = targetProduct;
        this.members = members;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTargetProduct() {
        return targetProduct;
    }

    public void setTargetProduct(String targetProduct) {
        this.targetProduct = targetProduct;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getGroupId() {
        return id;
    }

    public String getGroupName() {
        return name;
    }
}
