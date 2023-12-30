package com.example.app8.UIStudent;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.SearchView;
import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchBarActivity extends AppCompatActivity {

    private JoinedClassesManager joinedClassesManager;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_ui);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewJoinedClasses);
        SearchView searchView = findViewById(R.id.searchViewClasses);

        joinedClassesManager = new JoinedClassesManager(this, recyclerView);
        joinedClassesManager.filterClasses(searchView);
        connection = SQLConnection.getConnection();
        Intent intent = getIntent();
        String studentCode = null;
        if (intent != null && intent.hasExtra("CODE_STUDENT_KEY")) {
            studentCode = intent.getStringExtra("CODE_STUDENT_KEY");
        }

        final String finalStudentCode = studentCode;
        // Xử lý sự kiện khi chọn lớp học từ thanh tìm kiếm
        joinedClassesManager.setOnClassClickListener(className -> {
            String userName = getUsernameFromCodeStudent(finalStudentCode);
            String Email = getMailFromCodeStudent(finalStudentCode);
            byte[] imageData = getImageDataFromCodeStudent(finalStudentCode);
            Bitmap bitmap = convertByteArrayToBitmap(imageData);
            // Hiển thị hộp thoại "Tham gia lớp học"
            joinedClassesManager.showJoinClassDialog(userName, Email, bitmap, className, SearchBarActivity.this);
        });
    }
    private String getUsernameFromCodeStudent(String codeStudent) {
        String username = null;

        if (connection != null) {
            try {
                // Truy vấn SQL để lấy 'username' từ 'code_student'
                String query = "SELECT username FROM STUDENT_LIST WHERE code_student = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, codeStudent);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Kiểm tra xem có dữ liệu trả về không
                if (resultSet.next()) {
                    username = resultSet.getString("username");
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return username;
    }

    private String getMailFromCodeStudent(String codeStudent) {
        String username = null;

        if (connection != null) {
            try {
                // Truy vấn SQL để lấy 'username' từ 'code_student'
                String query = "SELECT email FROM STUDENT_LIST WHERE code_student = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, codeStudent);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Kiểm tra xem có dữ liệu trả về không
                if (resultSet.next()) {
                    username = resultSet.getString("email");
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return username;
    }
    private byte[] getImageDataFromCodeStudent(String codeStudent) {
        byte[] imageData = null;

        if (connection != null) {
            try {
                // Truy vấn SQL để lấy 'ImageData' từ 'code_student'
                String query = "SELECT ImageData FROM STUDENT_LIST WHERE code_student = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, codeStudent);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Kiểm tra xem có dữ liệu trả về không
                if (resultSet.next()) {
                    imageData = resultSet.getBytes("ImageData");
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return imageData;
    }
    public static Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        if (byteArray != null) {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } else {
            return null;
        }
    }
}
