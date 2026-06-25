package com.mudesuraj.studentmanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Auto-login: if user is already signed in, go straight to Dashboard
        if (mAuth.getCurrentUser() != null) {
            startActivity(
                    new Intent(
                            MainActivity.this,
                            DashboardActivity.class
                    )
            );
            finish();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){

                Toast.makeText(
                        MainActivity.this,
                        "Enter Email and Password",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mAuth.signInWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener(task -> {

                if(task.isSuccessful()){

                    Toast.makeText(
                            MainActivity.this,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    DashboardActivity.class
                            );

                    startActivity(intent);
                    finish();

                }else{

                    Toast.makeText(
                            MainActivity.this,
                            "Invalid Credentials",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        btnRegister.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(email.isEmpty() || password.isEmpty()){

                Toast.makeText(
                        MainActivity.this,
                        "Enter Email and Password",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            mAuth.createUserWithEmailAndPassword(
                    email,
                    password
            ).addOnCompleteListener(task -> {

                if(task.isSuccessful()){

                    Toast.makeText(
                            MainActivity.this,
                            "Registration Successful",
                            Toast.LENGTH_SHORT
                    ).show();

                }else{

                    Toast.makeText(
                            MainActivity.this,
                            task.getException().getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });
    }
}
