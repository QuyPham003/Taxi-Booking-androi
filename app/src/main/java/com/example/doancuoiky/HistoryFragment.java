package com.example.doancuoiky;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class HistoryFragment extends Fragment {

    private Button btnViewHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Khởi tạo nút
        btnViewHistory = view.findViewById(R.id.btnViewHistory);

        // Cài đặt sự kiện click cho nút
        btnViewHistory.setOnClickListener(v -> {
            // Chuyển sang HistoryActivity
            Intent intent = new Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
