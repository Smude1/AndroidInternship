package com.mudesuraj.studentmanagementapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.mudesuraj.studentmanagementapp.R;

public class StudentDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        Toolbar toolbar = findViewById(R.id.toolbarStudentDetails);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvRoll = findViewById(R.id.tvDetailRollNumber);
        TextView tvCourse = findViewById(R.id.tvDetailCourse);
        TextView tvSource = findViewById(R.id.tvDetailSource);
        MaterialButton btnEdit = findViewById(R.id.btnEditFromDetails);

        int studentId = getIntent().getIntExtra("student_id", -1);
        String name = getIntent().getStringExtra("student_name");
        String email = getIntent().getStringExtra("student_email");
        String roll = getIntent().getStringExtra("student_roll");
        String course = getIntent().getStringExtra("student_course");
        boolean isApi = getIntent().getBooleanExtra("student_api", false);

        tvName.setText(name);
        tvEmail.setText(email);
        tvRoll.setText(roll);
        tvCourse.setText(course);
        tvSource.setText(isApi ? getString(R.string.api_users) : getString(R.string.local_students));

        if (isApi || studentId <= 0) {
            btnEdit.setEnabled(false);
            btnEdit.setAlpha(0.5f);
        } else {
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddStudentActivity.class);
                intent.putExtra("student_id", studentId);
                startActivity(intent);
            });
        }
    }
}

