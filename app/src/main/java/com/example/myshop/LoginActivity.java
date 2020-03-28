package com.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myshop.Prevalent.RememberMe;
import com.google.cloud.audit.RequestMetadataOrBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.internal.$Gson$Preconditions;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private TextView forgotPass;
    private EditText phoneNumber, password;
    private Button login;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        forgotPass = findViewById(R.id.textView3);
        phoneNumber = (EditText)findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);
        login = (Button) findViewById(R.id.button);
        checkBox = (CheckBox)findViewById(R.id.checkBox);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"forgot",Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( !isFilled() ){
                    Toast.makeText(getApplicationContext(),"Please fill all the field...",Toast.LENGTH_LONG).show();
                    return ;
                }
                Paper.init(LoginActivity.this);

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Checking Account");
                progressDialog.setMessage("Please wait, while checking your account");
                progressDialog.show();

                checkInFirebaseDatabase();

            }
        });
    }

    void checkInFirebaseDatabase(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if( dataSnapshot.child(phoneNumber.getText().toString()).exists() ){
                    if( ( dataSnapshot.child(phoneNumber.getText().toString()).child("Password").getValue().toString() ).equals(password.getText().toString())){

                        if( checkBox.isChecked()){
                            Paper.book().write(RememberMe.phoneNumberKey, phoneNumber.getText().toString());
                            Paper.book().write(RememberMe.passwordKey, password.getText().toString());
                        }

                        Toast.makeText(getApplicationContext(),"You are Logged in ...",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                        Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                        startActivity(i);
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Password is InCorrect",Toast.LENGTH_LONG).show();
                    }

                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Phone number doesn't exist",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Error : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    boolean isFilled(){
        if(TextUtils.isEmpty(phoneNumber.getText().toString()))
            return  false;
        if(TextUtils.isEmpty(password.getText().toString()))
            return  false;

        return true;
    }

}
