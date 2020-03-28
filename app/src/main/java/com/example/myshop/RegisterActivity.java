package com.example.myshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.omadm.PpsMoParser;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myshop.Prevalent.RememberMe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class RegisterActivity extends AppCompatActivity {

    EditText emailId, phoneNo, password,fullName;
    Button register;
    DatabaseReference databaseReference;
    ProgressDialog pd;
    private CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailId = (EditText) findViewById(R.id.editText);
        phoneNo = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText2);
        register = (Button) findViewById(R.id.button);
        fullName = (EditText) findViewById(R.id.editText4);
        checkBox = (CheckBox) findViewById(R.id.checkBox2);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    void createAccount(){
        if( !isFilled()){
            Toast.makeText(getApplicationContext(),"please fill all the fields",Toast.LENGTH_LONG).show();
            return;
        }
        validatePhoneNumber();
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setTitle("Creating Account");
        pd.setMessage("Please wait while account is creating");
        pd.show();
    }

    boolean isFilled(){
        if(TextUtils.isEmpty(emailId.getText().toString()))
            return  false;
        if(TextUtils.isEmpty(phoneNo.getText().toString()))
            return  false;
        if(TextUtils.isEmpty(password.getText().toString()))
            return  false;

        return true;
    }
    void validatePhoneNumber(){
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if( !dataSnapshot.child(phoneNo.getText().toString()).exists()){
                        //DatabaseReference st = databaseReference.child(phoneNo.getText().toString());

                        Map<String,Object> map = new HashMap<>();
                        map.put("Full Name" , fullName.getText().toString());
                        map.put("Email Id" , emailId.getText().toString());
                        map.put("Password" , password.getText().toString());

                        Paper.init(RegisterActivity.this);

                        Task task = databaseReference.child(phoneNo.getText().toString()).setValue(map);

                        task.addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(getApplicationContext(),"  Yippie Account Created...",Toast.LENGTH_LONG).show();

                                if( checkBox.isChecked() ){
                                    Paper.book().write(RememberMe.phoneNumberKey,phoneNo.getText().toString());
                                    Paper.book().write(RememberMe.passwordKey,password.getText().toString());
                                }

                                pd.dismiss();
                                Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                                startActivity(i);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
                                        pd.dismiss();
                                    }
                                });

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"The phone number "+ phoneNo.getText().toString() +" is already exists \n Try another Phone Number",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();
                }
            });
    }
}
