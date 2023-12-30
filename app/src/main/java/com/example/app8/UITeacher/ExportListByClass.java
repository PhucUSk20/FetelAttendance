package com.example.app8.UITeacher;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;
import com.example.app8.UICommon.StudentInfo;
import com.google.gson.Gson;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExportListByClass extends AppCompatActivity {
    private Spinner classSpinner;
    private Spinner fileTypeSpinner;
    private ArrayAdapter<String> classAdapter;
    private ArrayAdapter<String> fileTypeAdapter;
    List<StudentInfo> st;
    private File fileexcel;
    private File filejson;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dropdown);

        classSpinner = findViewById(R.id.class_spinner);
        fileTypeSpinner = findViewById(R.id.file_type_spinner);
        Button exportButton = findViewById(R.id.export_button);
        connection = SQLConnection.getConnection();
        st = new ArrayList<>();
        setupClassSpinner();
        setupFileTypeSpinner();
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi nút Submit được nhấn
                String selectedClass = classSpinner.getSelectedItem().toString();
                String selectedFileType = fileTypeSpinner.getSelectedItem().toString();
                fileexcel = new File("/storage/self/primary/Download" + "/" + selectedClass + ".xls");
                filejson = new File("/storage/self/primary/Download" + "/" + selectedClass + ".json");
                st.clear();
                fetchStudentList(selectedClass);
                if (selectedFileType.equals("Excel")) {
                    try {
                        // Tạo workbook và sheet
                        HSSFWorkbook workbook = new HSSFWorkbook();
                        HSSFSheet sheet = workbook.createSheet("Data");

                        // Tạo row cho tiêu đề cột
                        HSSFRow headerRow = sheet.createRow(0);
                        // Assuming StudentInfo has corresponding getters for each field (getName, getCode, etc.)
                        String[] columnNames = {"name_student", "code_student", "date_of_birth", "attendance_date", "classId"};
                        for (int i = 0; i < columnNames.length; i++) {
                            headerRow.createCell(i).setCellValue(columnNames[i]);
                        }

                        // Tạo row cho từng bản ghi trong kết quả truy vấn
                        int rowNum = 1;
                        for (StudentInfo student : st) {
                            HSSFRow row = sheet.createRow(rowNum++);
                            row.createCell(0).setCellValue(student.getName());
                            row.createCell(1).setCellValue(student.getCode());
                            row.createCell(2).setCellValue(student.getDateOfBirth());
                            row.createCell(3).setCellValue(student.getAttendance_date());
                            row.createCell(4).setCellValue(student.getClassId());
                        }

                        if (fileexcel.exists()) {
                            // Delete the existing file
                            if (fileexcel.delete()) {
                                Log.d("FileDeleted", "Existing file delete successfully");
                                Toast.makeText(ExportListByClass.this, "File exists, delete old file", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("FileDeleted", "Failed to delete existing file");
                            }
                        }

                        // Lưu workbook vào tệp tin
                        try (FileOutputStream fileOut = new FileOutputStream(fileexcel)) {
                            workbook.write(fileOut);
                            Toast.makeText(ExportListByClass.this, "Export Excel Success", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(ExportListByClass.this, "Error Exporting to Excel", Toast.LENGTH_SHORT).show();
                        }

                        // Đóng workbook, ResultSet, statement và connection
                        try {
                            workbook.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (selectedFileType.equals("Json")) {
                    try {
                        st.clear();
                        fetchStudentList(selectedClass);
                        // Export the data to JSON
                        Gson gson = new Gson();
                        String jsonData = gson.toJson(st);

                        if (filejson.exists()) {
                            // Delete the existing file
                            if (filejson.delete()) {
                                Log.d("FileDeleted", "Existing file delete successfully");
                                Toast.makeText(ExportListByClass.this, "File exists, delete old file", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("FileDeleted", "Failed to delete existing file");
                            }
                        }

                        try {
                            FileWriter writer = new FileWriter(filejson);
                            writer.write(jsonData);
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ExportListByClass.this, "Export Json Success", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ExportListByClass.this, "Error exporting to JSON", Toast.LENGTH_SHORT).show();
                    }
                }
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

    private void setupFileTypeSpinner() {
        // Tạo adapter cho Spinner chọn loại file (Excel và Json)
        String[] fileTypes = {"Excel", "Json"};
        fileTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fileTypes);
        fileTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fileTypeSpinner.setAdapter(fileTypeAdapter);
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

    // Phương thức để lấy danh sách sinh viên từ cơ sở dữ liệu dựa trên lớp đã chọn
    // Modify fetchStudentList to use AsyncTask for fetching student list
    private void fetchStudentList(String selectedClass) {
        // Xóa dữ liệu cũ trong danh sách sinh viên
        st.clear();

        // Lấy ID của lớp từ tên lớp (name_subject)
        int classId = getIDFromNameSubject(selectedClass);

        // Nếu tìm thấy ID hợp lệ, thực hiện truy vấn để lấy danh sách sinh viên
        if (classId != -1) {
            new FetchStudentListTask().execute(classId);
        } else {
            // Xử lý khi không tìm thấy ID cho tên lớp đã chọn
            // Ví dụ: thông báo cho người dùng hoặc xử lý tùy thuộc vào logic ứng dụng của bạn
        }
    }

    // Sửa đổi AsyncTask để sử dụng ID lớp học thay vì tên lớp
    private class FetchStudentListTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... selectedClassIds) {
            int selectedClassId = selectedClassIds[0];
            // Thực hiện truy vấn để lấy danh sách sinh viên từ cơ sở dữ liệu dựa trên selectedClassId
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT name_student, code_student, date_of_birth, attendance_date, classId " +
                                "FROM ATTENDANCE WHERE classId = ?");
                preparedStatement.setInt(1, selectedClassId);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String name = resultSet.getString("name_student");
                    String code = resultSet.getString("code_student");
                    String dateOfBirth = resultSet.getString("date_of_birth");
                    String attendance_date = resultSet.getString("attendance_date");
                    int classId = resultSet.getInt("classId");

                    StudentInfo st2 = new StudentInfo(name, code, dateOfBirth, attendance_date, classId);
                    st.add(st2);
                }

                resultSet.close();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // Cập nhật giao diện người dùng hoặc thực hiện bất kỳ công việc sau khi thực hiện nếu cần
        }
    }

    // Sửa đổi phương thức để lấy ID từ tên lớp
    private int getIDFromNameSubject(String selectedClass) {
        int id = -1; // Giá trị mặc định nếu không tìm thấy

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id FROM CLASS WHERE name_subject = ?");
            preparedStatement.setString(1, selectedClass);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Kiểm tra nếu có kết quả từ truy vấn
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }

            resultSet.close(); // Đóng ResultSet
            preparedStatement.close(); // Đóng PreparedStatement
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }
}
