package com.example.app8.UICommon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.app8.R;

import java.util.ArrayList;

public class CustomStudentListAdapterCommon extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> studentList;
    private ArrayList<byte[]> imageDataList; // Danh sách dữ liệu hình ảnh

    public CustomStudentListAdapterCommon(Context context, ArrayList<String> studentList, ArrayList<byte[]> imageDataList) {
        super(context, R.layout.student_list_custom, studentList);
        this.context = context;
        this.studentList = studentList;
        this.imageDataList = imageDataList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.student_list_custom, parent, false);
        }
        //Lấy dữ liệu
        String studentName = studentList.get(position);
        byte[] imageData = imageDataList.get(position);
        // Gán dữ liệu
        TextView studentNameTextView = convertView.findViewById(R.id.studentNameTextView);
        ImageView studentImageView = convertView.findViewById(R.id.studentImageView);
        studentNameTextView.setText(studentName);
        // Chuyển dữ liệu byte[] thành hình ảnh và gán cho ImageView
        if (imageData != null && imageData.length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            studentImageView.setImageBitmap(bitmap);
        }
        return convertView;
    }
}
