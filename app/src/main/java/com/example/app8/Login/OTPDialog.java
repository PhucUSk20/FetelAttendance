package com.example.app8.Login;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.chaos.view.PinView;
import com.example.app8.R;

public class OTPDialog extends Dialog {

    private String user;
    private int code;
    private Context context;

    public OTPDialog(@NonNull Context context, int code, String user) {
        super(context);
        this.context = context;
        this.code = code;
        this.user = user;
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_otpdialog);

        // Initialize UI components and set values based on the code and user
        setupViews();
    }

    private void setupViews() {
        Button btnconfirm = findViewById(R.id.btnconfirm);
        PinView pinView = findViewById(R.id.pinview);
        TextView display_signup = findViewById(R.id.display_signup);
        Window window = getWindow();

        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            window.setAttributes(windowAttributes);
            window.setWindowAnimations(R.style.DialogAnimation);
        }

        display_signup.setText(String.valueOf(code));

        btnconfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = pinView.getText().toString().trim();

                if (inputCode.equals(String.valueOf(code))) {
                    showResetPasswordDialog(user);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "Incorrect code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showResetPasswordDialog(String user) {
        if (context != null && context instanceof AppCompatActivity) {
            ResetPassword dialogFragment = new ResetPassword();
            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.OTPDialogTheme);
            Bundle args = new Bundle();
            args.putString("STRING_VALUE", user);
            dialogFragment.setArguments(args);
            AppCompatActivity activity = (AppCompatActivity) context;
            dialogFragment.setActivityReference(OTPDialog.this);
            dialogFragment.show(activity.getSupportFragmentManager(), "ResetPassword");
        }
    }
}
