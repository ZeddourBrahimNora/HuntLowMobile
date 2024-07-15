package com.example.huntlow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.concurrent.TimeUnit;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerViewProducts;
    private EditText editTextSearch;
    private Button buttonSearch;
    private ArrayList<String> productNames;
    private ArrayList<String> nutriScores;
    private ArrayList<String> productImages;
    private ProductAdapter productAdapter;

    private static final String TAG = "SearchFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        editTextSearch = view.findViewById(R.id.searchEditText);
        buttonSearch = view.findViewById(R.id.searchButton);

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productNames = new ArrayList<>();
        nutriScores = new ArrayList<>();
        productImages = new ArrayList<>();
        productAdapter = new ProductAdapter(productNames, nutriScores, productImages);
        recyclerViewProducts.setAdapter(productAdapter);

        if (savedInstanceState != null) {
            productNames = savedInstanceState.getStringArrayList("productNames");
            nutriScores = savedInstanceState.getStringArrayList("nutriScores");
            productImages = savedInstanceState.getStringArrayList("productImages");
            if (productNames != null && nutriScores != null && productImages != null) {
                productAdapter.updateData(productNames, nutriScores, productImages);
            }
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
        outState.putStringArrayList("nutriScores", nutriScores);
        outState.putStringArrayList("productImages", productImages);
    }

    void searchProductsByName(String productName) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        String url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + productName + "&search_simple=1&action=process&json=true";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(TAG, "Request Failed", e);
                if (e instanceof java.net.SocketTimeoutException) {
                    Log.e(TAG, "Connection timed out. Retrying...");
                    searchProductsByName(productName);
                } else {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateUI(myResponse));
                    }
                } else {
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }
        });
    }

    void updateUI(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray products = jsonObject.getJSONArray("products");
            productNames.clear();
            nutriScores.clear();
            productImages.clear();
            for (int i = 0; i < products.length(); i++) {
                JSONObject product = products.getJSONObject(i);
                String productName = product.getString("product_name");
                String nutriScore = product.optString("nutriscore_grade", "N/A");
                String productImage = product.optString("image_url", "");
                productNames.add(productName);
                nutriScores.add(nutriScore);
                productImages.add(productImage);
            }
            productAdapter.updateData(productNames, nutriScores, productImages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
