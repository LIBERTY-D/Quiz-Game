package com.daniel.quizgame.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daniel.quizgame.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

public class GameOver extends AppCompatActivity {


    private TextView totalCorrect,totalWrong;
    Button retry,quit;


    FirebaseDatabase firebaseDatabase =  FirebaseDatabase.getInstance();
    DatabaseReference databaseReference =  firebaseDatabase.getReference().child("scores");
    FirebaseAuth auth =  FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    DatabaseReference userScore = databaseReference.child(user.getUid());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        totalWrong = (TextView) findViewById(R.id.totalWrong);
        totalCorrect = (TextView) findViewById(R.id.totalCorrect);

        retry = (Button) findViewById(R.id.retry);
        quit = (Button) findViewById(R.id.quit);

        Intent i =  getIntent();
        int correct =  i.getIntExtra("correct",-1);
        int wrongs =  i.getIntExtra("wrong",-1);
        totalCorrect.setText("correct: "+ correct);
        totalWrong.setText("incorrect: "+wrongs);



        userScore.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                   snapshot.child("correct").getRef().setValue(correct);
                   snapshot.child("wrong").getRef().setValue(wrongs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =  new Intent(GameOver.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });



        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }
}