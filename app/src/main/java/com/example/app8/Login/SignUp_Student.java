package com.example.app8.Login;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUp_Student extends AppCompatActivity {
    Button btnsignup;
    TextView display_signup, check_user, check_pass, check_confirm, check_email;
    Connection connect = null;
    private EditText username_signup, email_signup, password_signup, confirm_signup, code_student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_student);

        btnsignup = findViewById(R.id.btnsignup);
        username_signup = findViewById(R.id.username_signup);
        password_signup = findViewById(R.id.password_signup);
        confirm_signup = findViewById(R.id.confirm_signup);
        code_student = findViewById(R.id.code_student);
        email_signup = findViewById(R.id.email_signup);

        check_user = findViewById(R.id.check_user);
        check_pass = findViewById(R.id.check_pass);
        check_confirm = findViewById(R.id.check_confirm);
        check_email = findViewById(R.id.check_email);

        SQLConnection b = new SQLConnection();
        connect = b.getConnection();

        btnsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signup();
            }

            public void signup() {
                String username = username_signup.getText().toString().trim();
                String password = password_signup.getText().toString().trim();
                String confirm = confirm_signup.getText().toString().trim();
                String email = email_signup.getText().toString().trim();
                String code = code_student.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    check_user.setText("Tên đăng nhập là bắt buộc");
                }else if(isUsernameExists(username)){
                    check_user.setText("Tên đăng nhập đã tồn tại!");
                }else if (TextUtils.isEmpty(email)) {
                    check_email.setText("Email là bắt buộc");
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    check_email.setText("Định dạng sai!!!");
                }else if (TextUtils.isEmpty(password)) {
                    check_pass.setText("Mật khẩu là bắt buộc");
                }else if (TextUtils.isEmpty(confirm)) {
                    check_confirm.setText("Vui lòng xác nhận mật khẩu");
                }else if (!password.equals(confirm)) {
                    check_confirm.setText("Mật khẩu và xác nhận phải giống nhau!");
                }else{

                    if (addAccount(username, password, email, code)) {
                        Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUp_Student.this, StudentLogin.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
                    } else {
                        Toast.makeText(getApplicationContext(), "Đăng ký tài khoản thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    }}
            }

            //Hàm kiểm tra username có tồn tại trong database hay không
            private boolean isUsernameExists(String username) {
                boolean check = false;
                try {
                    String query = "SELECT 1 FROM giangvien WHERE username = ? UNION SELECT 1 FROM STUDENT_LIST WHERE username = ?";
                    PreparedStatement ps = connect.prepareStatement(query);
                    ps.setString(1, username);
                    ps.setString(2, username);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()){
                        check = true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUp_Student.this, "Lỗi truy vấn cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                }
                return check;
            }

            private boolean addAccount(String username, String password, String email, String codestudent) {
                try {
                    String query = "INSERT INTO STUDENT_LIST (username, password, email, code_student) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = connect.prepareStatement(query);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, email);
                    ps.setString(4, codestudent);

                    int rowsInserted = ps.executeUpdate();

                    if (rowsInserted > 0) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUp_Student.this, "Lỗi truy vấn cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }
}