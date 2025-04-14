package com.example.doancuoiky;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class login_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String phone = etPhoneNumber.getText().toString();
            String password = etPassword.getText().toString();

            if (phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else if (dbHelper.checkUser(phone, password)) {
                // Lấy tên người dùng từ cơ sở dữ liệu
                String userName = dbHelper.getUserName(phone);

                // Lưu thông tin người dùng vào SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", userName);  // Lưu tên người dùng
                editor.putString("phone", phone);    // Lưu số điện thoại
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                // Chuyển sang màn hình chính
                Intent intent = new Intent(login_activity.this, MainActivity.class);
                startActivity(intent);
                finish();  // Đảm bảo màn hình đăng nhập không quay lại khi nhấn back
            } else {
                Toast.makeText(this, "Sai số điện thoại hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
            }
        });

        tvCreateAccount.setOnClickListener(v -> {
            Intent intent = new Intent(login_activity.this, create_account.class);
            startActivity(intent);
        });
    }
}
