package com.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myshop.Prevalent.RememberMe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private Button login,register;
    //private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        Paper.init(MainActivity.this);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        String paperPhoneNumber = Paper.book().read(RememberMe.phoneNumberKey);
        String paperPassword = Paper.book().read(RememberMe.passwordKey);

        if( !TextUtils.isEmpty(paperPassword) && ! TextUtils.isEmpty(paperPhoneNumber)){
            checkInFirebaseDatabase(paperPhoneNumber,paperPassword);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });


    }

    void checkInFirebaseDatabase(final String phone, final String pass){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.child(phone).exists() ){
                    if( ( dataSnapshot.child(phone).child("Password").getValue().equals(pass))){

                        Toast.makeText(getApplicationContext(),"You are Logged in ...",Toast.LENGTH_SHORT).show();
                      //  progressDialog.dismiss();
                        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(i);
                    }
                    else{
                    //    progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Password is InCorrect",Toast.LENGTH_LONG).show();
                    }

                }
                else {
                  //  progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Phone number doesn't exist",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}
