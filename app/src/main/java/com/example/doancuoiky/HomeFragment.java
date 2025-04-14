package com.example.doancuoiky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout for HomeFragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Find the TextViews and Button by ID
        TextView tvGreeting = view.findViewById(R.id.tvGreeting);
        TextView tvPhone = view.findViewById(R.id.tvPhone); // Add a TextView for the phone number
        Button btnGoToTaxi4 = view.findViewById(R.id.button);

        // Get the SharedPreferences to retrieve the user's name and phone number
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String userName = sharedPreferences.getString("name", "user"); // Default "user" if no name is saved
        String userPhone = sharedPreferences.getString("phone", ""); // Default "" if no phone is saved

        // Set the greeting message to include the user's name
        tvGreeting.setText("Xin chào, " + userName + "!");
        // Display the user's phone number in the second TextView
        tvPhone.setText("Số điện thoại: " + userPhone);

        // Set up the button click listener to navigate to Taxi4Activity
        btnGoToTaxi4.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Taxi4Activity.class);
            startActivity(intent);
        });

        return view;
    }
}
