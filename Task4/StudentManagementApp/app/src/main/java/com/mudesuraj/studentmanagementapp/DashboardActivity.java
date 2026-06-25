package com.mudesuraj.studentmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    Button btnAddStudent;
    Button btnLogout;

    ListView listView;
    ArrayList<String> studentList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        listView = findViewById(R.id.listViewStudents);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnLogout = findViewById(R.id.btnLogout);

        // Setup list and adapter
        studentList = new ArrayList<>();
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                studentList
        );
        listView.setAdapter(adapter);

        // Fetch users from API
        ApiService apiService =
                RetrofitClient.getInstance()
                        .create(ApiService.class);

        Call<List<User>> call = apiService.getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (User user : response.body()) {
                        studentList.add(user.getName() + "\n" + user.getEmail());
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this,
                        "Failed to load users",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this,
                    AddStudentActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(
                    DashboardActivity.this,
                    MainActivity.class
            );

            startActivity(intent);
            finish();
        });
    }
}