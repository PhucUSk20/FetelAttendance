package com.example.app8.Login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;
import com.example.app8.UITeacher.UI_Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LecturerLogin extends AppCompatActivity {
    private EditText editText3;
    private EditText editText4;
    private TextView check_username, check_password;
    private Button btnlogingv, signupButton, forgotpasswordButton_gv,back_gv;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_login);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        btnlogingv = findViewById(R.id.btnlogingv);
        back_gv = findViewById(R.id.back_gv);
        check_username = findViewById(R.id.check_username);
        check_password = findViewById(R.id.check_password);
        forgotpasswordButton_gv = findViewById(R.id.forgotpasswordButton_gv);
        signupButton = findViewById(R.id.signupButton);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        connection = SQLConnection.getConnection();
    }

    public void Login(View view) {
        String username = editText3.getText().toString();
        String password = editText4.getText().toString();

        // Kiểm tra kết nối cơ sở dữ liệu
        if (connection == null) {
            Toast.makeText(LecturerLogin.this, "Không thể kết nối đến cơ sở dữ liệu.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            check_username.setText("Vui lòng nhập tên đăng nhập!");
        }else if(UsernameNotExist(username)){
            check_username.setText("Tên đăng nhập không tồn tại!");
        }else if (TextUtils.isEmpty(password)) {
            check_password.setText("Vui lòng nhập mật khẩu!");
        }else{
        boolean authenticated = authenticateUser(username, password);

        if (authenticated) {
            // Nếu đăng nhập thành công, chuyển sang Activity tiếp theo
            Toast.makeText(LecturerLogin.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            // User is a teacher, go to TeacherActivity
            Intent intent = new Intent();
            intent = new Intent(LecturerLogin.this, UI_Teacher.class);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
        } else {
            // Hiển thị thông báo lỗi nếu đăng nhập không thành công
            check_password.setText("Mật khẩu không đúng!");
            Toast.makeText(LecturerLogin.this, "Đăng nhập không thành công. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }}
    }

    private boolean UsernameNotExist(String username) {
        boolean check = false;
        try {
            String query = "SELECT COUNT(*) FROM giangvien WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count <= 0){
                check = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            check = true;
        }
        return check;
    }

    public boolean authenticateUser(String username, String password) {
        boolean authenticated = false;
        // Thực hiện truy vấn kiểm tra tài khoản trong cơ sở dữ liệu
        // Thay thế dòng sau bằng cách sử dụng PreparedStatement để tránh lỗi SQL Injection
        String query = "SELECT COUNT(*) FROM giangvien WHERE USERNAME = ? AND PASSWORD = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next(); // Di chuyển con trỏ đến dòng đầu tiên

            int count = resultSet.getInt(1);

            if (count == 1) {
                authenticated = true;
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            // Ghi log lỗi
            Log.e("MyApp", "Lỗi xảy ra: " + e.getMessage());
        }
        return authenticated;
    }

    public void forgotPassword_(View view) {
        Intent i = new Intent(LecturerLogin.this, ForgotPassword.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void signup_account(View view) {
        Intent i = new Intent(LecturerLogin.this, SignUp_Teacher.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }
    public void comeback(View view) {
        Intent intent = new Intent(LecturerLogin.this, AdminLogin.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LecturerLogin.this, AdminLogin.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

}
