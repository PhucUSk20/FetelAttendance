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
import com.example.app8.UIStudent.UI_Student;
import com.example.app8.UIStudent.StudentRegisterActivity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentLogin extends AppCompatActivity {
    private EditText editText1;
    private EditText editText2;
    private TextView check_username, check_password;
    private Button btnloginsv, signupButton, forgotpasswordButton, back_sv;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_login);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        btnloginsv = findViewById(R.id.btnloginsv);
        check_username = findViewById(R.id.check_username);
        check_password = findViewById(R.id.check_password);
        forgotpasswordButton = findViewById(R.id.forgotpasswordButton);
        signupButton = findViewById(R.id.signupButton);
        back_sv = findViewById(R.id.back_sv);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        connection = SQLConnection.getConnection();
    }

    public void Login(View view) {
        String username = editText1.getText().toString();
        String password = editText2.getText().toString();

        // Kiểm tra kết nối cơ sở dữ liệu
        if (connection == null) {
            Toast.makeText(StudentLogin.this, "Không thể kết nối đến cơ sở dữ liệu.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(username)) {
            check_username.setText("Vui lòng nhập tên đăng nhập!");
        } else if (UsernameNotExist(username)) {
            check_username.setText("Tên đăng nhập không tồn tại!");
        } else if (TextUtils.isEmpty(password)) {
            check_password.setText("Vui lòng nhập mật khẩu!");
        } else {
            boolean authenticated = authenticateUser(username, password);

            if (authenticated) {
                // Kiểm tra giá trị name_student từ cột username
                String nameStudent = retrieveNameStudent(username);
                String code = retrieveCodeStudent(username);
                if (nameStudent != null) {
                    // Nếu có giá trị name_student khác NULL, chuyển sang UI_Student
                    Toast.makeText(StudentLogin.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentLogin.this, UI_Student.class);
                    intent.putExtra("CODE_STUDENT_KEY", code);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                } else {
                    // Nếu giá trị name_student là NULL, chuyển sang StudentRegisterActivity
                    Toast.makeText(StudentLogin.this, "Vui lòng hoàn thiện thông tin sinh viên!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(StudentLogin.this, StudentRegisterActivity.class);
                    intent.putExtra("USERNAME_KEY", username);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                }
            } else {
                // Hiển thị thông báo lỗi nếu đăng nhập không thành công
                check_password.setText("Mật khẩu không đúng!");
                Toast.makeText(StudentLogin.this, "Đăng nhập thất bại!. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String retrieveNameStudent(String username) {
        String nameStudent = null;
        try {
            String query = "SELECT name_student FROM STUDENT_LIST WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nameStudent = rs.getString("name_student");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nameStudent;
    }
    private String retrieveCodeStudent(String username) {
        String codeStudent = null;
        try {
            String query = "SELECT code_student FROM STUDENT_LIST WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                codeStudent = rs.getString("code_student");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return codeStudent;
    }


    private boolean UsernameNotExist(String username) {
        boolean check = false;
        try {
            String query = "SELECT COUNT(*) FROM STUDENT_LIST WHERE username = ?";
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
        String query = "SELECT COUNT(*) FROM STUDENT_LIST WHERE username = ? AND password = ?";

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

    public void forgotPassword(View view) {
        Intent i = new Intent(StudentLogin.this, ForgotPassword.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void signup_account(View view) {
        Intent i = new Intent(StudentLogin.this, SignUp_Student.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    public void comeback(View view) {
        Intent intent = new Intent(StudentLogin.this, AdminLogin.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(StudentLogin.this, AdminLogin.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }


}
