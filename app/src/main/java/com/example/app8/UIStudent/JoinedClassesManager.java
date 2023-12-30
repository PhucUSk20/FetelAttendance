package com.example.app8.UIStudent;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JoinedClassesManager {

    private Context context;
    private RecyclerView recyclerView;
    private CustomJoinedClassesAdapter adapter;
    private List<String> joinedClasses;
    private Connection connection;
    private int selectedClassId;

    public interface OnClassClickListener {
        void onClassClick(String className);
    }

    private OnClassClickListener onClassClickListener;

    public void setOnClassClickListener(OnClassClickListener listener) {
        this.onClassClickListener = listener;
    }

    public JoinedClassesManager(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.joinedClasses = new ArrayList<>();
        this.connection = SQLConnection.getConnection();
        initRecyclerView();
    }

    private void initRecyclerView() {
        adapter = new CustomJoinedClassesAdapter(context, joinedClasses);
        adapter.setOnClassClickListener(className -> {
            if (onClassClickListener != null) {
                onClassClickListener.onClassClick(className);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }
    private int getClassIdFromDatabase(String className) {
        int classId = -1;
        String query = "SELECT id FROM CLASS WHERE name_subject = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, className);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                classId = resultSet.getInt("id");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classId;
    }
    public void filterClasses(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<String> filteredList = new ArrayList<>();
        String query = "SELECT name_subject FROM CLASS WHERE name_subject LIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + text + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String className = resultSet.getString("name_subject");
                filteredList.add(className);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.filterList(filteredList);
    }

    public void addJoinedClass(String className) {
        joinedClasses.add(className);
        adapter.notifyDataSetChanged();
    }
    public boolean addStudentToClass(String classId, String studentCode) {
        boolean success = false;
        Connection connection = SQLConnection.getConnection(); // Lấy kết nối đến cơ sở dữ liệu
        if (connection != null) {
            try {
                String query = "INSERT INTO THAMGIA (classid, code_student) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, Integer.parseInt(classId)); // Chuyển đổi String classId thành int
                preparedStatement.setString(2, studentCode);
                int rowsAffected = preparedStatement.executeUpdate();
                preparedStatement.close();

                if (rowsAffected > 0) {
                    success = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Log.e("SQL_ERROR", "Lỗi SQL: " + e.getMessage()); // Ghi log lỗi SQL
            }
        }
        return success;
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public List<String> getJoinedClasses(String studentCode) {
        List<String> joinedClassesList = new ArrayList<>();
        String query = "SELECT name_subject FROM CLASS WHERE id IN (SELECT classid FROM THAMGIA WHERE code_student = ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, studentCode);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String className = resultSet.getString("name_subject");
                joinedClassesList.add(className);
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return joinedClassesList;
    }
    public void showJoinClassDialog(String studentName, String studentEmail, Bitmap studentImage, String classname, Context context) {
        // Inflate layout for the dialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_join_class, null);

        // Initialize views in the dialog
        ImageView imageViewStudent = dialogView.findViewById(R.id.imageViewStudent);
        TextView textViewClass = dialogView.findViewById(R.id.textViewClass);
        TextView textViewStudentName = dialogView.findViewById(R.id.textViewStudentName);
        TextView textViewStudentEmail = dialogView.findViewById(R.id.textViewStudentEmail);
        EditText editTextClassCode = dialogView.findViewById(R.id.editTextClassCode);
        Button buttonJoinClass = dialogView.findViewById(R.id.buttonJoinClass);

        // Set student name and email
        textViewStudentName.setText(studentName);
        textViewStudentEmail.setText(studentEmail);
        imageViewStudent.setImageBitmap(studentImage);
        textViewClass.setText(classname);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // Create the AlertDialog object and show it
        AlertDialog dialog = builder.create();
        dialog.show();

        // Logic for handling "Tham gia" button click
        buttonJoinClass.setOnClickListener(v -> {
            String classCode = editTextClassCode.getText().toString().trim();
            String classId = String.valueOf(getClassIdFromClassName(classname)); // Get class ID from CLASS table based on class name
            String studentCode = getCodeStudentFromUsername(studentName);
            Log.d("JoinedClassesManager", "Class name: " + classCode);
            Log.d("JoinedClassesManager", "Class ID: " + classId);
            if (classId.equals(classCode)) {
                boolean joined = addStudentToClass(classId, studentCode); // Add student to THAMGIA table
                if (joined) {
                    showToast(context, "Tham gia lớp học thành công");
                    List<String> joinedClasses = getJoinedClasses(studentCode);
                    adapter.updateClasses(joinedClasses);
                } else {
                    showToast(context, "Tham gia lớp học thất bại");
                }
            } else {
                showToast(context, "Không tìm thấy mã lớp học");
            }
            dialog.dismiss();
        });
    }
    private int getClassIdFromClassName(String className) {
        int classId = -1;
        String query = "SELECT id FROM CLASS WHERE name_subject = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, className);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                classId = resultSet.getInt("id");
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classId;
    }


    private String getCodeStudentFromUsername(String username) {
        String codeStudent = null;

        if (connection != null) {
            try {
                // Truy vấn SQL để lấy 'code_student' từ 'username'
                String query = "SELECT code_student FROM STUDENT_LIST WHERE username = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Kiểm tra xem có dữ liệu trả về không
                if (resultSet.next()) {
                    codeStudent = resultSet.getString("code_student");
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return codeStudent;
    }

}
