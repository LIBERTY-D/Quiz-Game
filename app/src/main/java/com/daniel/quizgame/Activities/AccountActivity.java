package com.daniel.quizgame.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daniel.quizgame.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.security.Permission;

public class AccountActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView displayEmail,displayName;

    private ImageDecoder.Source selectedImage;

    private Uri imageUri;



    private final int  REQUEST_CODE = 1;
    private FirebaseAuth auth =  FirebaseAuth.getInstance();
    private FirebaseUser user = auth.getCurrentUser();

    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);


        storageReference = FirebaseStorage.getInstance().getReference(user.getUid());
        registerPickImageLauncher();
        imageView = (ImageView) findViewById(R.id.displayImage);
        displayEmail = (TextView) findViewById(R.id.displayEmail);
        displayName = (TextView) findViewById(R.id.displayName);



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String permissions =   permission();

                if(ContextCompat.checkSelfPermission(getApplicationContext(),permissions)!= PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(AccountActivity.this,new String[]{permissions},REQUEST_CODE
                    );
                }else{
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickImageLauncher.launch(i);

                }

            }
        });


    }
    public String permission() {
        String permissions;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = Manifest.permission.READ_MEDIA_IMAGES;

        } else {
            permissions = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        return permissions;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_CODE && grantResults.length>0&& grantResults[0] ==PackageManager.PERMISSION_GRANTED){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(i);
        }
    }

    public void registerPickImageLauncher(){
        pickImageLauncher =  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                 Intent i =  result.getData();
                 int rescode =  result.getResultCode();

                 if(i!=null && rescode==RESULT_OK){
                     imageUri =i.getData();
                     Picasso.get().load(imageUri).into(imageView);
                     uploadImageToFireBase(imageUri);

                 }
            }
        });

    }

    public  void uploadImageToFireBase(Uri imageUri){
        StorageReference sr =  storageReference.child("profile");
        sr.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                  sr.getDownloadUrl().addOnCompleteListener(AccountActivity.this,new OnCompleteListener<Uri>() {
                      @Override
                      public void onComplete(@NonNull Task<Uri> task) {

                          if(task.isSuccessful()){
                              Uri image = task.getResult();

                              UserProfileChangeRequest userProfileChangeRequest =  new UserProfileChangeRequest.Builder().setPhotoUri(image).build();
                              user.updateProfile(userProfileChangeRequest).addOnCompleteListener(AccountActivity.this, new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {

                                      Toast.makeText(AccountActivity.this,"upload success",Toast.LENGTH_SHORT).show();

                                  }
                              });
                          }

                      }
                  });




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AccountActivity.this,"Failed to upload",Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri image = user.getPhotoUrl();
        String email = user.getEmail();
        String name =  user.getDisplayName();
        Picasso.get().load(image).into( imageView);
        displayEmail.setText(email);
        displayName.setText(name);


    }
}

