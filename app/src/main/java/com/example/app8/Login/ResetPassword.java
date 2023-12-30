package com.example.app8.Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.app8.R;
import com.example.app8.SQLServer.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResetPassword extends DialogFragment {

    Connection connection;
    String user, password;
    private EditText pass_reset, confirm_reset;
    private TextView check_pass_reset, check_confirm_reset, display_reset;
    private Button btnok_reset;
    private OTPDialog activityReference;
    public void setActivityReference(OTPDialog activity) {
        this.activityReference = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set the theme of the dialog to match the theme of the OTPDialog
        int style = R.style.OTPDialogTheme; // Replace with the actual style of OTPDialog

        if (getActivity() != null) {
            // Use the activity's theme instead of the changing configurations
            Context activityContext = getActivity();
            if (activityContext != null) {
                style = activityContext.getTheme().getChangingConfigurations();
            }
        }

        // Create a custom context themed with the OTPDialog theme
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), style);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        // Inflate the layout for this fragment

        View view = localInflater.inflate(R.layout.activity_reset_password, container, false);
        // Add your view initialization and configuration here

        if (getArguments() != null) {
            user = getArguments().getString("STRING_VALUE", "default_value_if_not_found");
            // Use the user value as needed
        }
        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Set the custom animation for the dialog
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;

        // Apply the theme for the OTPDialog
        int style = R.style.OTPDialogTheme;

        // Create a custom context themed with the OTPDialog theme
        Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), style);
        LayoutInflater localInflater = getActivity().getLayoutInflater().cloneInContext(contextThemeWrapper);

        // Inflate the layout for this fragment
        View view = localInflater.inflate(R.layout.activity_reset_password, null, false);

        // Add your view initialization and configuration here
        dialog.setContentView(view);

        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pass_reset = view.findViewById(R.id.pass_reset);
        confirm_reset = view.findViewById(R.id.confirm_reset);
        check_pass_reset = view.findViewById(R.id.check_pass_reset);
        check_confirm_reset = view.findViewById(R.id.check_confirm_reset);
        display_reset = view.findViewById(R.id.display_reset);
        btnok_reset = view.findViewById(R.id.btnok_reset);
        //user = getActivity().getIntent().getStringExtra("STRING_VALUE");


        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttributes = getDialog().getWindow().getAttributes();
            windowAttributes.gravity = Gravity.RIGHT; // Assuming right is the direction you want
            getDialog().getWindow().setAttributes(windowAttributes);
        }

        connection = SQLConnection.getConnection(); // Assuming SQLConnection is a class to handle database connections

        if (connection != null) {
            display_reset.setText("SUCCESSFUL CONNECTION");
        } else {
            display_reset.setText("ERROR");
        }

        btnok_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p_rs = pass_reset.getText().toString().trim();
                String confirm = confirm_reset.getText().toString().trim();
                if (TextUtils.isEmpty(p_rs)) {
                    check_pass_reset.setText("Bạn chưa nhập mật khẩu!");
                }
                else if (TextUtils.isEmpty(confirm)) {
                    check_confirm_reset.setText("Vui lòng xác nhận mật khẩu!");
                }
                else if (!p_rs.equals(confirm)) {
                    check_confirm_reset.setText("Mật khẩu và Xác nhận phải giống nhau!");
                }else{

                try {
                    String query = "SELECT password FROM giangvien WHERE username = ? UNION SELECT password FROM STUDENT_LIST WHERE username = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, user);
                    preparedStatement.setString(2, user);

                    ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    password = resultSet.getString("password");
                    try {
                        String updateGiangVien = "UPDATE giangvien SET password = ? WHERE username = ?";
                        PreparedStatement psGiangVien = connection.prepareStatement(updateGiangVien);
                        psGiangVien.setString(1, p_rs);
                        psGiangVien.setString(2, user);
                        int rowsUpdatedGiangVien = psGiangVien.executeUpdate();
                        psGiangVien.close();

                        String updateSinhVien = "UPDATE STUDENT_LIST SET password = ? WHERE username = ?";
                        PreparedStatement psSinhVien = connection.prepareStatement(updateSinhVien);
                        psSinhVien.setString(1, p_rs);
                        psSinhVien.setString(2, user);
                        int rowsUpdatedSinhVien = psSinhVien.executeUpdate();
                        psSinhVien.close();

                        if (rowsUpdatedGiangVien > 0 || rowsUpdatedSinhVien > 0) {
                            Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(getContext(), AdminLogin.class);
                            startActivity(in);
                            dismiss();
                            getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
                            //create an animation for returning AdminLogin Activity
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Đổi mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                    }
                    resultSet.close();
                    preparedStatement.close();
                    }else{
                    display_reset.setText("username is not found!");
                }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi khi truy vấn cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
                }
            }}
        });
    }
}
