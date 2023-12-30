package com.example.app8.Login;

import android.os.Bundle;
import android.text.TextUtils;
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
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {
    public int code;
    public String user;
    Connection connect;
    private EditText userText;
    private TextView check_UserText, check_connect;
    private Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        userText = findViewById(R.id.userText);
        check_UserText = findViewById(R.id.check_UserText);
        check_connect = findViewById(R.id.check_connect);
        reset = findViewById(R.id.reset);
        connect = SQLConnection.getConnection();

        if (connect != null) {
            check_connect.setText("SUCCESSFUL CONNECTION WITH SQL SERVER");
        } else {
            check_connect.setText("ERROR!");
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "";
                user = userText.getText().toString().trim();
                if (TextUtils.isEmpty(user)) {
                    check_UserText.setText("Vui lòng nhập tên đăng nhập!");
                } else{
                if (isValidUser(user)) {
                    try {
                        // Construct a parameterized query to retrieve the email for the given username
                        String query = "SELECT email FROM giangvien WHERE username = ? UNION SELECT email FROM STUDENT_LIST WHERE username = ?";
                        PreparedStatement preparedStatement = connect.prepareStatement(query);
                        preparedStatement.setString(1, user);
                        preparedStatement.setString(2, user);

                        // Execute the query and get the result set
                        ResultSet resultSet = preparedStatement.executeQuery();

                        if (resultSet.next()) {
                            // Assuming at least one result is found, retrieve the email
                            email = resultSet.getString("email");
                            resultSet.close();
                            preparedStatement.close();
                            check_UserText.setText(email);
                            checkSendEmail(email);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(ForgotPassword.this, "Lỗi khi truy vấn cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    check_UserText.setText("Tên đăng nhập không tồn tại!");
                }

            }}
        });
    }

    public void checkSendEmail(String email) throws MessagingException {
        Random random = new Random();
        code = random.nextInt(8999) + 1000;
        if (sendEmail("Attendance of Face Recognition App", "Ma OTP:" + code, email)) {
            // Email sent successfully
            Toast.makeText(ForgotPassword.this, "Đã gửi mail thành công", Toast.LENGTH_SHORT).show();
            showDialog();
        } else {
            Toast.makeText(ForgotPassword.this, "Gửi mail thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean sendEmail(String subject, String content, String email) throws MessagingException {
        String senderEmail = "attendancesystemlalala@gmail.com";
        String password = "thqmsiytompognpg"; //attendance@123
        Properties props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email, false));
        message.setSubject(subject);
        message.setText(content);
        try {
            Transport.send(message);
            return true;
        } catch (AddressException e) {
            e.printStackTrace();
            Toast.makeText(ForgotPassword.this, "Address Exception", Toast.LENGTH_SHORT).show();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            Toast.makeText(ForgotPassword.this, "Messaging Exception", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isValidUser(String user) {
        boolean authenticated = false;
        String query = "SELECT 1 FROM giangvien WHERE username = ? UNION SELECT 1 FROM STUDENT_LIST WHERE username = ?";

        try (
                PreparedStatement preparedStatement = connect.prepareStatement(query)) {
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, user);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    authenticated = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authenticated;
    }

    public void showDialog() {
        OTPDialog otpDialog = new OTPDialog(ForgotPassword.this, code, user);
        otpDialog.show();
    }
}