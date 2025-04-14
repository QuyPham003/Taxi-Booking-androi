package com.example.doancuoiky;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class create_account extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        EditText etName = findViewById(R.id.edtName);
        EditText etPhone = findViewById(R.id.etNewPhoneNumber);
        EditText etPassword = findViewById(R.id.etNewPassword);
        EditText etConfirmPassword = findViewById(R.id.etConfirmPassword);
        Button btnCreateAccount = findViewById(R.id.btnCreateAccount);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        btnCreateAccount.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
            } else {
                boolean isAdded = dbHelper.addUser(phone, password, name);
                if (isAdded) {
                    // Lưu tên vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", name);
                    editor.putString("phone", phone);
                    editor.apply();

                    Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // Đóng màn hình đăng ký và quay lại màn hình đăng nhập
                } else {
                    Toast.makeText(this, "Lỗi khi đăng ký tài khoản!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
