package com.daniel.quizgame.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daniel.quizgame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {


    private EditText signUpPassword,signUpEmail;



    private Button signUpButton;

    private ProgressBar progressBar;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        signUpPassword = (EditText) findViewById(R.id.signUpPassword);
        signUpEmail = (EditText) findViewById(R.id.signUpEmail);


        signUpButton = (Button) findViewById(R.id.signUpButton);


        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpButton.setClickable(false);
                String email =  signUpEmail.getText().toString();
                String passwd = signUpPassword.getText().toString();
                signUpEmailPasswordFirebase(email,passwd);
            }
        });

    }


    public  void signUpEmailPasswordFirebase(String email,String password){
        progressBar.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Toast.makeText(SignUpActivity.this, "created account successfully", Toast.LENGTH_SHORT)
                            .show();
                    finish();
                    progressBar.setVisibility(View.INVISIBLE);
                }else {
                    Toast.makeText(SignUpActivity.this, "Try again, something went wrong", Toast.LENGTH_SHORT)
                            .show();
                    Log.d("exception", String.valueOf(task.getException()));
                }
            }
        });



    }
}