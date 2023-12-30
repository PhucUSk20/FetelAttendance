package com.example.app8.UITeacher;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExportListByDate extends AppCompatActivity {
    private Spinner classSpinner;
    private Connection connection;
    private ArrayAdapter<String> classAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        classSpinner = findViewById(R.id.class_spinner_2);
        connection = SQLConnection.getConnection();
        setupClassSpinner();
        CalendarView calendarView = findViewById(R.id.calenderView);
        final TextView selectedDay = findViewById(R.id.selectedDay);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                String selectedClass = classSpinner.getSelectedItem().toString();

                // Create an Intent
                Intent intent = new Intent(ExportListByDate.this, AttendListActivity.class);

                // Pass the selected date as extras
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("dayOfMonth", dayOfMonth);
                intent.putExtra("selectedClass", selectedClass);
                // Start the TestActivity
                startActivity(intent);
            }
        });
    }
    private void setupClassSpinner() {
        // Tạo kết nối đến cơ sở dữ liệu và lấy danh sách các lớp
        List<String> classList = getClassListFromDatabase();

        // Tạo adapter cho Spinner chọn class
        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);
    }
    private List<String> getClassListFromDatabase() {
        List<String> classList = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT name_subject FROM CLASS");

            while (resultSet.next()) {
                String className = resultSet.getString("name_subject");
                classList.add(className);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return classList;
    }
}