package com.example.huntlow;

import androidx.lifecycle.ViewModel;
import java.util.List;
import com.github.mikephil.charting.data.BarEntry;

public class ProfileViewModel extends ViewModel {
    private boolean isInitialized = false;
    private long openCount = 0;
    private List<BarEntry> entries;
    private List<String> labels;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public long getOpenCount() {
        return openCount;
    }

    public void incrementOpenCount() {
        openCount++;
    }

    public void setOpenCount(long openCount) {
        this.openCount = openCount;
    }

    public List<BarEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<BarEntry> entries) {
        this.entries = entries;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
