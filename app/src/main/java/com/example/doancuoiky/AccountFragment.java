package com.example.doancuoiky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    private TextView tvCustomerName, tvPhoneNumber;
    private Button btnLogout, btnChangeName, btnChangePassword, btnConfirmChangeName, btnConfirmChangePassword;
    private EditText etNewName, etNewPassword;
    private DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        tvCustomerName = view.findViewById(R.id.tvCustomerName);
        tvPhoneNumber = view.findViewById(R.id.tvPhoneNumber);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnChangeName = view.findViewById(R.id.btnChangeName); // Nút đổi tên
        btnChangePassword = view.findViewById(R.id.btnChangePassword); // Nút đổi mật khẩu
        etNewName = view.findViewById(R.id.etNewName); // EditText nhập tên mới
        etNewPassword = view.findViewById(R.id.etNewPassword); // EditText nhập mật khẩu mới
        btnConfirmChangeName = view.findViewById(R.id.btnConfirmChangeName); // Nút xác nhận đổi tên
        btnConfirmChangePassword = view.findViewById(R.id.btnConfirmChangePassword); // Nút xác nhận đổi mật khẩu

        // Ẩn các EditText và nút xác nhận ban đầu
        etNewName.setVisibility(View.GONE);
        etNewPassword.setVisibility(View.GONE);
        btnConfirmChangeName.setVisibility(View.GONE);
        btnConfirmChangePassword.setVisibility(View.GONE);

        // Lấy SharedPreferences và DatabaseHelper
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        String userPhone = sharedPreferences.getString("phone", "");
        dbHelper = new DatabaseHelper(getContext());

        // Hiển thị thông tin người dùng
        String userName = dbHelper.getUserName(userPhone);
        tvCustomerName.setText("Tên người dùng: " + userName);
        tvPhoneNumber.setText("Số điện thoại: " + userPhone);

        // Nút đổi tên
        btnChangeName.setOnClickListener(v -> {
            etNewName.setVisibility(View.VISIBLE); // Hiển thị ô nhập tên mới
            btnConfirmChangeName.setVisibility(View.VISIBLE); // Hiển thị nút xác nhận đổi tên
            btnChangeName.setVisibility(View.GONE); // Ẩn nút đổi tên
        });

        // Nút đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            etNewPassword.setVisibility(View.VISIBLE); // Hiển thị ô nhập mật khẩu mới
            btnConfirmChangePassword.setVisibility(View.VISIBLE); // Hiển thị nút xác nhận đổi mật khẩu
            btnChangePassword.setVisibility(View.GONE); // Ẩn nút đổi mật khẩu
        });

        // Nút xác nhận đổi tên
        btnConfirmChangeName.setOnClickListener(v -> {
            String newName = etNewName.getText().toString();
            if (newName.isEmpty()) {
                Toast.makeText(getContext(), "Tên không được để trống!", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.updateUserName(userPhone, newName);
                if (success) {
                    tvCustomerName.setText("Tên người dùng: " + newName);
                    Toast.makeText(getContext(), "Đổi tên thành công!", Toast.LENGTH_SHORT).show();
                    etNewName.setVisibility(View.GONE); // Ẩn ô nhập tên mới
                    btnConfirmChangeName.setVisibility(View.GONE); // Ẩn nút xác nhận đổi tên
                    btnChangeName.setVisibility(View.VISIBLE); // Hiển thị lại nút đổi tên
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra, thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút xác nhận đổi mật khẩu
        btnConfirmChangePassword.setOnClickListener(v -> {
            String newPassword = etNewPassword.getText().toString();
            if (newPassword.isEmpty()) {
                Toast.makeText(getContext(), "Mật khẩu không được để trống!", Toast.LENGTH_SHORT).show();
            } else {
                boolean success = dbHelper.updateUserPassword(userPhone, newPassword);
                if (success) {
                    Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    etNewPassword.setVisibility(View.GONE); // Ẩn ô nhập mật khẩu mới
                    btnConfirmChangePassword.setVisibility(View.GONE); // Ẩn nút xác nhận đổi mật khẩu
                    btnChangePassword.setVisibility(View.VISIBLE); // Hiển thị lại nút đổi mật khẩu
                } else {
                    Toast.makeText(getContext(), "Có lỗi xảy ra, thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getContext(), login_activity.class);
            startActivity(intent);
            requireActivity().finish();
        });
    }
}
