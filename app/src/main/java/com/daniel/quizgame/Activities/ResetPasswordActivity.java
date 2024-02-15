package com.daniel.quizgame.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.daniel.quizgame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {


    private EditText resetEmail;

    private Button resetPasswordButton;

    private FirebaseAuth auth  = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        resetEmail = (EditText) findViewById(R.id.resetEmail);

        resetPasswordButton = (Button) findViewById(R.id.resetPasswordButton);


        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email =  resetEmail.getText().toString();
                resetPasswordFireBase(email);

            }
        });
    }


    public  void resetPasswordFireBase(String email){
         auth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
             @Override
             public void onComplete(@NonNull Task<Void> task) {
                 if(task.isSuccessful()){
                     Toast.makeText(ResetPasswordActivity.this,
                             "An email has been sent",Toast.LENGTH_SHORT).show();
                     finish();
                     resetPasswordButton.setClickable(false);
                 }else{
                     Toast.makeText(ResetPasswordActivity.this,
                             "Failed to send email",Toast.LENGTH_SHORT).show();
                 }

             }
         });
    }
}