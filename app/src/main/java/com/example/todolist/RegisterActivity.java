//Student Name: Megan Cash
//Student Number: C19317723
package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    //Initialising Variables
    TextView alreadyHaveAccount;
    EditText inputEmail,inputPassword,inputFullName, inputPhoneNumber;
    Button registerButton;
    ProgressDialog progressDialog;

    //Create instances of the Firebase Database
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("To Do List");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);

        inputEmail=findViewById(R.id.inputEmail);
        inputPassword=findViewById(R.id.inputPassword);
        inputFullName=findViewById(R.id.inputFullName);
        inputPhoneNumber=findViewById(R.id.inputPhoneNumber);
        registerButton=findViewById(R.id.registerButton);
        progressDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();



        //Initialise 'Already have an Account' textView
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            }
        });

        //Initialise Register Button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformAuthentication();
            }
        });
    }
    private void PerformAuthentication() {
        String email = inputEmail.getText().toString();
        String fullName = inputFullName.getText().toString();
        String phoneNumber = inputPhoneNumber.getText().toString();
        String password = inputPassword.getText().toString();




        //Validation
        if (email.isEmpty()) { //To ensure that the email field is not empty.
            inputEmail.setError("Error! Please enter a valid email.");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { // To ensure the email address is valid.
            inputEmail.setError("Error! Please enter a valid email.");
            inputEmail.requestFocus();
            return;
        } else if (password.isEmpty() || password.length() < 6) { //To ensure the password field is not empty & is more than 6 characters.
            inputPassword.setError("Error! Your password must be over 6 characters.");
        } else if (fullName.isEmpty()) { //To ensure full name field is not empty.
            inputFullName.setError("Error! Please enter your full name");
        } else if (phoneNumber.isEmpty()) {
            inputPhoneNumber.setError("Error! Please enter your phone number");
        } else {
            progressDialog.setMessage("Loading...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

           mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
              public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        User user = new User(email, password, fullName, phoneNumber);
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            goToNextActivity();
                                            Toast.makeText(RegisterActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Registration Unsuccessful!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration Unsuccessful!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                   }

                }

                private void goToNextActivity() { //To send user from Register Page to the Home Page.
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }
    }
}