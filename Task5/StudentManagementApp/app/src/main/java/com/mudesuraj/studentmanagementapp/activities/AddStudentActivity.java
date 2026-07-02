package com.mudesuraj.studentmanagementapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.mudesuraj.studentmanagementapp.R;
import com.mudesuraj.studentmanagementapp.database.StudentDatabase;
import com.mudesuraj.studentmanagementapp.models.Student;
import com.mudesuraj.studentmanagementapp.utils.ValidationUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddStudentActivity extends AppCompatActivity {

    private TextInputEditText etStudentName;
    private TextInputEditText etRollNumber;
    private TextInputEditText etEmail;
    private TextInputEditText etCourse;
    private MaterialButton btnSave;
    private ProgressBar progressBar;

    private StudentDatabase studentDatabase;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private int studentId = -1;
    private Student editingStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        Toolbar toolbar = findViewById(R.id.toolbarAddStudent);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        studentDatabase = StudentDatabase.getInstance(this);

        initViews();
        readIntent();

        btnSave.setOnClickListener(v -> saveOrUpdateStudent());
    }

    private void initViews() {
        etStudentName = findViewById(R.id.etStudentName);
        etRollNumber = findViewById(R.id.etRollNumber);
        etEmail = findViewById(R.id.etStudentEmail);
        etCourse = findViewById(R.id.etCourse);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressSaveStudent);
    }

    private void readIntent() {
        studentId = getIntent().getIntExtra("student_id", -1);

        if (studentId > 0) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.edit_student_title);
            }
            btnSave.setText(R.string.update);
            loadStudentForEdit();
        }
    }

    private void loadStudentForEdit() {
        setSavingState(true);
        databaseExecutor.execute(() -> {
            Student student = studentDatabase.studentDao().getStudentById(studentId);
            runOnUiThread(() -> {
                setSavingState(false);
                editingStudent = student;
                if (student == null) {
                    Toast.makeText(this, R.string.failed_to_load_student, Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                etStudentName.setText(student.getName());
                etRollNumber.setText(student.getRollNumber());
                etEmail.setText(student.getEmail());
                etCourse.setText(student.getCourse());
            });
        });
    }

    private void saveOrUpdateStudent() {
        String name = String.valueOf(etStudentName.getText()).trim();
        String rollNumber = String.valueOf(etRollNumber.getText()).trim();
        String email = String.valueOf(etEmail.getText()).trim();
        String course = String.valueOf(etCourse.getText()).trim();

        if (!validateInput(name, rollNumber, email, course)) {
            return;
        }

        setSavingState(true);
        databaseExecutor.execute(() -> {
            if (studentId > 0 && editingStudent != null) {
                editingStudent.setName(name);
                editingStudent.setRollNumber(rollNumber);
                editingStudent.setEmail(email);
                editingStudent.setCourse(course);
                studentDatabase.studentDao().updateStudent(editingStudent);
                runOnUiThread(() -> {
                    setSavingState(false);
                    Toast.makeText(this, R.string.student_updated, Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            Student student = new Student(name, rollNumber, email, course);
            student.setFromApi(false);
            studentDatabase.studentDao().insertStudent(student);

            runOnUiThread(() -> {
                setSavingState(false);
                Toast.makeText(this, R.string.student_saved, Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    private boolean validateInput(String name, String rollNumber, String email, String course) {
        if (!ValidationUtils.isValidStudentName(name)) {
            etStudentName.setError(getString(R.string.empty_name_error));
            return false;
        }

        if (!ValidationUtils.isValidRollNumber(rollNumber)) {
            etRollNumber.setError(getString(R.string.empty_roll_error));
            return false;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError(getString(R.string.invalid_email_error));
            return false;
        }

        if (!ValidationUtils.isValidCourse(course)) {
            etCourse.setError(getString(R.string.empty_course_error));
            return false;
        }

        return true;
    }

    private void setSavingState(boolean isSaving) {
        progressBar.setVisibility(isSaving ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isSaving);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}

