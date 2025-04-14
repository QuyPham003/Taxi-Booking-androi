package com.example.doancuoiky;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private ListView listViewHistory;
    private ArrayAdapter<String> adapter;
    private List<String> historyList;
    private Button buttonDeleteAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        buttonDeleteAll = findViewById(R.id.buttonDeleteAll);
        historyList = new ArrayList<>();

        // Lấy lịch sử từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("TaxiHistory", MODE_PRIVATE);
        String json = sharedPreferences.getString("history", "[]");

        // Chuyển chuỗi JSON thành ArrayList
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        historyList = gson.fromJson(json, type);

        // Cài đặt adapter cho ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
        listViewHistory.setAdapter(adapter);

        // Thiết lập sự kiện long press để xoá item
        listViewHistory.setOnItemLongClickListener((parent, view, position, id) -> {
            // Xoá item trong danh sách
            String itemToRemove = historyList.get(position);
            historyList.remove(position);

            // Cập nhật adapter
            adapter.notifyDataSetChanged();

            // Lưu lại dữ liệu mới vào SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String updatedJson = new Gson().toJson(historyList);
            editor.putString("history", updatedJson);
            editor.apply();

            // Hiển thị thông báo cho người dùng
            Toast.makeText(HistoryActivity.this, "Đã xoá: " + itemToRemove, Toast.LENGTH_SHORT).show();

            return true;  // Return true to indicate that the event was handled
        });

        // Thiết lập sự kiện cho nút "Xoá Tất Cả"
        buttonDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xoá tất cả các mục trong danh sách
                historyList.clear();

                // Cập nhật adapter
                adapter.notifyDataSetChanged();

                // Cập nhật lại SharedPreferences với danh sách trống
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("history", "[]");
                editor.apply();

                // Hiển thị thông báo cho người dùng
                Toast.makeText(HistoryActivity.this, "Đã xoá tất cả lịch sử", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
