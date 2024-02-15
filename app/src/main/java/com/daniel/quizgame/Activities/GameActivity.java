package com.daniel.quizgame.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.quizgame.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameActivity extends AppCompatActivity {



    private TextView write,wrong,life,time,quest;

    private TextView a,b,c,d;
    private String opta,optb,optc,optd;

    private Button quitG,next;

    private final int TOTAL_MILLIS =  60000;

    private int left = TOTAL_MILLIS;

    private CountDownTimer timer;

    private LinearLayout layoutMain;
    private int numberOfQuestions;

    private  String useranswerCharacter="";
    private String correctFromDbAnswer="";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Questions");
    private int count=1;

    private int correctCount;

    private int wrongCount;

   private  int userLife=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);


        layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
        a = (TextView) findViewById(R.id.a);

        b= (TextView) findViewById(R.id.b);
        c = (TextView) findViewById(R.id.c);
        d = (TextView) findViewById(R.id.d);

        answerClick(a);
        answerClick(b);
        answerClick(c);
        answerClick(d);

        startTimer();
        write = (TextView) findViewById(R.id.write);
        wrong = (TextView) findViewById(R.id.wrong);

        life = (TextView) findViewById(R.id.life);
        time= (TextView) findViewById(R.id.time);

        quest = (TextView) findViewById(R.id.quest);



        quitG = (Button) findViewById(R.id.quitG);
        next = (Button) findViewById(R.id.next);



        loadQuestions();
        resetLIfe();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDefault();
                loadQuestions();
                disableOrEnableTexViews(true);
                resetTimer();

            }
        });

        quitG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(GameActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
    public  void answerClick(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                resetDefault();
                CardView parent = (CardView) textView.getParent();
                int id =  textView.getId();
                if(id==R.id.a){
                    useranswerCharacter = "a";
                    changeBackGround(parent);

                } else if (id==R.id.b) {
                     useranswerCharacter ="b";
                    changeBackGround(parent);
                } else if (id==R.id.c) {
                     useranswerCharacter ="c";
                    changeBackGround(parent);
                } else if (id==R.id.d) {
                    useranswerCharacter = "d";
                    changeBackGround(parent);
                }
                disableOrEnableTexViews(false);

            }
        });
    }

    private void startTimer(){
        timer =  new CountDownTimer(TOTAL_MILLIS,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                left = (int) (millisUntilFinished/1000);
                updateUI();
            }
            @Override
            public void onFinish() {
               quest.setText("Sorry You ran out of time!!!");

               disableOrEnableTexViews(false);
            }
        }.start();

    }

private void resetDefault(){
        for (int i=0 ; i<layoutMain.getChildCount();i++){
            View view =  layoutMain.getChildAt(i);
            if(view instanceof  CardView){
                CardView  parent = (CardView) view;
                parent.setCardBackgroundColor(Color.WHITE);
                View child =  parent.getChildAt(0);
                if(child  instanceof TextView){
                    TextView textView = (TextView) child;
                    textView.setTextColor(Color.BLUE);
                }
            }
        }
}

private void disableOrEnableTexViews(boolean val){
    for (int i=0 ; i<layoutMain.getChildCount();i++){
        View view =  layoutMain.getChildAt(i);
        if(view instanceof  CardView){
            CardView  parent = (CardView) view;
            View child =  parent.getChildAt(0);
            if(child  instanceof TextView){
                TextView textView = (TextView) child;
                textView.setClickable(val);
            }

        }
    }
}
    private void resetTimer(){
        timer.cancel();
         startTimer();
    }
    private void   updateUI(){
        time.setText("Time: "+left);
        if(userLife<=0){
            Intent i =  new Intent(GameActivity.this,GameOver.class);
            i.putExtra("correct",correctCount);
            i.putExtra("wrong",wrongCount);
            startActivity(i);

            finish();
            timer.cancel();
        }

    }
    private  void success(CardView parent){
        parent.setCardBackgroundColor(Color.GREEN);

    }

    private  void danger(CardView parent){
        parent.setCardBackgroundColor(Color.RED);
    }

    private  void changeBackGround(CardView parent){
        if(useranswerCharacter.equals(correctFromDbAnswer)){
            success(parent);
            correctCount = correctCount+1;
            write.setText("Correct: "+correctCount);

        }else{
            danger(parent);
            wrongCount = wrongCount+1;
            userLife =  userLife-1;
            life.setText("Life: "+ userLife);
            wrong.setText("Incorrect: "+wrongCount);

        }
    }

    public  void resetLIfe(){
        userLife = 3;
        life.setText("Life: "+userLife);

    }

    private void loadQuestions(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numberOfQuestions = (int) snapshot.getChildrenCount();
                if(count<=numberOfQuestions){
                    DataSnapshot data = snapshot.child(String.valueOf(count));
                    String question = (String) data.child("q").getValue();
                    DataSnapshot options =  data.child("options");


                    opta = String.valueOf(options.child("a").getValue());
                    optb = String.valueOf(options.child("b").getValue());
                    optc = String.valueOf(options.child("c").getValue());
                    optd = String.valueOf(options.child("d").getValue());

                    correctFromDbAnswer = String.valueOf(data.child("answer").getValue());

                    quest.setText(question);

                    a.setText(opta);
                    b.setText(optb);
                    c.setText(optc);
                    d.setText(optd);
                    count=count+1;
                }else{
                    Intent i =  new Intent(GameActivity.this,GameOver.class);
                    i.putExtra("correct",correctCount);
                    i.putExtra("wrong",wrongCount);
                    startActivity(i);
                    timer.cancel();
                    finish();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GameActivity.this,"Failed to read data",Toast.LENGTH_SHORT).show();
            }
        });
    }



}