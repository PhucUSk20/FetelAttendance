package com.example.app8.Login;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.app8.R;

public class LoginDialog extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_dialog);
        showDialog();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
    }

    // Method to close the dialog
    public void showDialog() {
        final Dialog dialog = new Dialog(LoginDialog.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_login_dialog);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);
            window.setWindowAnimations(R.style.DialogAnimation);
        }
        Button qlButton = dialog.findViewById(R.id.qlButton);
        Button gvButton = dialog.findViewById(R.id.gvButton);
        Button svButton = dialog.findViewById(R.id.svButton);

        qlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(AdminLogin.class);
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });

        gvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(LecturerLogin.class);
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });

        svButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchActivity(StudentLogin.class);
                dialog.dismiss(); // Dismiss the dialog when the button is clicked
            }
        });
        dialog.show();
    }

    private void launchActivity(Class<?> activityClass) {
        Intent intent = new Intent(LoginDialog.this, activityClass);
        startActivity(intent);
    }
}

