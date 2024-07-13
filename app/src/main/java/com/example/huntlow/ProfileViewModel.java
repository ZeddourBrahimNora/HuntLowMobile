package com.example.huntlow;

import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private List<BarEntry> entries = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    private boolean isInitialized = false;

    public List<BarEntry> getEntries() {
        return entries;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setEntries(List<BarEntry> entries) {
        this.entries = entries;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }
}
