package com.mudesuraj.studentmanagementapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.mudesuraj.studentmanagementapp.R;
import com.mudesuraj.studentmanagementapp.adapters.StudentAdapter;
import com.mudesuraj.studentmanagementapp.database.StudentDatabase;
import com.mudesuraj.studentmanagementapp.models.Student;
import com.mudesuraj.studentmanagementapp.models.User;
import com.mudesuraj.studentmanagementapp.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements StudentAdapter.StudentActionListener {

    private RecyclerView recyclerView;
    private EditText etSearch;
    private TextView tvEmptyState;
    private TextView tvStudentCount;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialButton btnAddStudent;
    private MaterialButton btnLogout;

    private StudentAdapter studentAdapter;
    private StudentDatabase studentDatabase;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    private final List<Student> localStudents = new ArrayList<>();
    private final List<Student> apiStudents = new ArrayList<>();
    private final List<Student> allStudents = new ArrayList<>();

    private boolean localLoaded;
    private boolean apiLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbarDashboard);
        setSupportActionBar(toolbar);

        studentDatabase = StudentDatabase.getInstance(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        refreshData();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewStudents);
        etSearch = findViewById(R.id.etSearchStudent);
        tvEmptyState = findViewById(R.id.tvEmptyState);
        tvStudentCount = findViewById(R.id.tvStudentCount);
        progressBar = findViewById(R.id.progressDashboard);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupRecyclerView() {
        studentAdapter = new StudentAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(studentAdapter);
    }

    private void setupListeners() {
        btnAddStudent.setOnClickListener(v -> startActivity(new Intent(this, AddStudentActivity.class)));

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_confirm)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void refreshData() {
        localLoaded = false;
        apiLoaded = false;

        setLoadingState(true);

        loadLocalStudents();
        loadApiStudents();
    }

    private void loadLocalStudents() {
        databaseExecutor.execute(() -> {
            List<Student> studentsFromDb = studentDatabase.studentDao().getAllLocalStudents();
            runOnUiThread(() -> {
                localStudents.clear();
                localStudents.addAll(studentsFromDb);
                localLoaded = true;
                tryPublishStudents();
            });
        });
    }

    private void loadApiStudents() {
        RetrofitClient.getApiService().getUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                apiStudents.clear();
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        Student student = new Student();
                        student.setId(-user.getId());
                        student.setName(user.getName());
                        student.setEmail(user.getEmail());
                        student.setCourse(getString(R.string.api_user_label));
                        student.setRollNumber(String.valueOf(user.getId()));
                        student.setFromApi(true);
                        apiStudents.add(student);
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, R.string.failed_to_load, Toast.LENGTH_SHORT).show();
                }

                apiLoaded = true;
                tryPublishStudents();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                apiStudents.clear();
                apiLoaded = true;
                Toast.makeText(DashboardActivity.this, getString(R.string.failed_to_load), Toast.LENGTH_SHORT).show();
                tryPublishStudents();
            }
        });
    }

    private void tryPublishStudents() {
        if (!localLoaded || !apiLoaded) {
            return;
        }

        allStudents.clear();
        allStudents.addAll(localStudents);
        allStudents.addAll(apiStudents);

        tvStudentCount.setText(getString(R.string.students_count, allStudents.size()));
        filterStudents(String.valueOf(etSearch.getText()));

        setLoadingState(false);
    }

    private void filterStudents(String query) {
        String searchText = query == null ? "" : query.trim().toLowerCase();
        List<Student> filtered = new ArrayList<>();

        for (Student student : allStudents) {
            if (searchText.isEmpty()
                    || safeContains(student.getName(), searchText)
                    || safeContains(student.getEmail(), searchText)
                    || safeContains(student.getRollNumber(), searchText)
                    || safeContains(student.getCourse(), searchText)) {
                filtered.add(student);
            }
        }

        studentAdapter.submitList(filtered);
        tvEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private boolean safeContains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(isLoading);
        btnAddStudent.setEnabled(!isLoading);
        btnLogout.setEnabled(!isLoading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }

    @Override
    public void onView(Student student) {
        Intent intent = new Intent(this, StudentDetailsActivity.class);
        intent.putExtra("student_id", student.getId());
        intent.putExtra("student_name", student.getName());
        intent.putExtra("student_email", student.getEmail());
        intent.putExtra("student_roll", student.getRollNumber());
        intent.putExtra("student_course", student.getCourse());
        intent.putExtra("student_api", student.isFromApi());
        startActivity(intent);
    }

    @Override
    public void onEdit(Student student) {
        if (student.isFromApi()) {
            Toast.makeText(this, R.string.api_user_edit_not_allowed, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, AddStudentActivity.class);
        intent.putExtra("student_id", student.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(Student student) {
        if (student.isFromApi()) {
            return;
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_student_confirm)
                .setPositiveButton(R.string.delete, (dialog, which) -> databaseExecutor.execute(() -> {
                    studentDatabase.studentDao().deleteStudent(student);
                    runOnUiThread(() -> {
                        Toast.makeText(this, R.string.student_deleted, Toast.LENGTH_SHORT).show();
                        refreshData();
                    });
                }))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void onFavoriteToggle(Student student) {
        if (student.isFromApi()) {
            return;
        }

        student.setFavorite(!student.isFavorite());
        databaseExecutor.execute(() -> {
            studentDatabase.studentDao().updateStudent(student);
            runOnUiThread(this::refreshData);
        });
    }
}

