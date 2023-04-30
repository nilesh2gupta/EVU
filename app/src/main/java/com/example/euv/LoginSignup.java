package com.example.euv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginSignup extends AppCompatActivity {
    FirebaseAuth auth;
    EditText useremail,password;
    Button login;
    TextView signup,forgetpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);

        auth=FirebaseAuth.getInstance();
        useremail=findViewById(R.id.useremail);
        password=findViewById(R.id.password);
        login=findViewById(R.id.loginbtn);
        signup=findViewById(R.id.signup);
        forgetpassword=findViewById(R.id.forgetpassword);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e=useremail.getText().toString();
                String p=password.getText().toString();
                auth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginSignup.this,Dashboard.class));
                        }
                        else{

                            Toast.makeText(LoginSignup.this, "Check your credentials", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e=useremail.getText().toString();
                String p=password.getText().toString();
                auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(LoginSignup.this, "Account is created successfully!!", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Toast.makeText(LoginSignup.this, "Already signed up!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user=auth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LoginSignup.this,Dashboard.class));
            finish();
        }
    }
}