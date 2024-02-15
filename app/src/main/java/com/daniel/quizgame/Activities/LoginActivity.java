package com.daniel.quizgame.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.quizgame.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;



public class LoginActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 100;
    private EditText signInPassword, loginEmail;
    private TextView forgot_password, signup_click;
    private SignInButton googleSignInButton;

    private Button regular_sign_in;

    private ProgressBar progressBarLogin;
    public static GoogleSignInClient googleSignInClient;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private ActivityResultLauncher<Intent> googleIntentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInPassword = (EditText) findViewById(R.id.signInPassword);
        loginEmail = (EditText) findViewById(R.id.loginEmail);

        forgot_password = (TextView) findViewById(R.id.forgot_password);
        signup_click = (TextView) findViewById(R.id.signup_click);

        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

        progressBarLogin.setVisibility(View.INVISIBLE);
        googleSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);


        regular_sign_in = (Button) findViewById(R.id.regular_sign_in);
        googleIntentLauncherRegister();


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(i);
            }
        });


        signup_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);

            }
        });


        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogleFireBase();
            }
        });


        regular_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String password = signInPassword.getText().toString();
                signInEmailPasswordFirebase(email, password);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
     if(user!=null){
         Intent i =  new Intent(LoginActivity.this,MainActivity.class);
         startActivity(i);
         finish();

     }


    }

    public void signInEmailPasswordFirebase(String email, String password){


        progressBarLogin.setVisibility(View.VISIBLE);
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT)
                                .show();
                        Intent i  = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(i);
//                        finish();

                    }else{
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT)
                                .show();
                    }
                    progressBarLogin.setVisibility(View.INVISIBLE);

                }
            });

    }


    public  void  googleIntentLauncherRegister(){
        googleIntentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult res) {

                int rescode =  res.getResultCode();
                Intent data =  res.getData();
                if(rescode==RESULT_OK && data !=null){
                    Task<GoogleSignInAccount> task =GoogleSignIn.getSignedInAccountFromIntent(data);
                    fireBaseGoogleSignInTask(task);
                }
            }
        });
    }
    public  void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleIntentLauncher.launch(signInIntent);
    }
    public  void signInWithGoogleFireBase(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();

    }
    public void fireBaseGoogleSignInTask(   Task<GoogleSignInAccount> task){
         GoogleSignInAccount account =  task.getResult();
         Toast.makeText(LoginActivity.this,
                 "sucessfully signed in",Toast.LENGTH_SHORT).show();
         Intent i =  new Intent(LoginActivity.this,MainActivity.class);
         startActivity(i);
         googleAccount(account);

    }

    public  void googleAccount( GoogleSignInAccount account){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful()){
                     Toast.makeText(LoginActivity.this,
                             "success",Toast.LENGTH_SHORT).show();


                 }else{
                     Toast.makeText(LoginActivity.this,
                             "failed",Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }

}