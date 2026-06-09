package com.mudesuraj.studentmanagementapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddStudentActivity extends AppCompatActivity {

    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            Toast.makeText(
                    AddStudentActivity.this,
                    "Student Saved Successfully!",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}