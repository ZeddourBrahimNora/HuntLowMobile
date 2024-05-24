package com.example.huntlow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Call;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private ListView listViewProducts;
    private EditText editTextSearch;
    private Button buttonSearch;
    private ArrayList<String> productNames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        listViewProducts = view.findViewById(R.id.listViewProducts);
        editTextSearch = view.findViewById(R.id.searchEditText);
        buttonSearch = view.findViewById(R.id.searchButton);

        if (savedInstanceState != null) {
            productNames = savedInstanceState.getStringArrayList("productNames");
            if (productNames != null) {
                updateListView(productNames);
            }
        } else {
            productNames = new ArrayList<>();
        }

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = editTextSearch.getText().toString();
                if (!searchTerm.isEmpty()) {
                    searchProductsByName(searchTerm);
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("productNames", productNames);
    }

    void searchProductsByName(String productName) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + productName + "&search_simple=1&action=process&json=true";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateUI(myResponse));
                    }
                }
            }
        });
    }

    void updateUI(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray products = jsonObject.getJSONArray("products");
            productNames.clear();
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String productName = product.getString("product_name");
                productNames.add(productName);
            }
            updateListView(productNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateListView(ArrayList<String> productNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, productNames);
        listViewProducts.setAdapter(adapter);
    }
}
