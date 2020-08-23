package example.android.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class LoginActivity extends AppCompatActivity {
    private TextView reg_btn;
    private EditText emailbtn;
    private EditText passwordbtn;
    private Button signinbtn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    String getemail;
    String getpass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.LOgintoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        emailbtn = findViewById(R.id.login_email);
        passwordbtn = findViewById(R.id.login_pass);
        signinbtn = findViewById(R.id.signbtn);
        reg_btn = findViewById(R.id.register_btn);


        signinbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getemail = emailbtn.getText().toString();
                getpass = passwordbtn.getText().toString();

                if (!TextUtils.isEmpty(getemail) || !TextUtils.isEmpty(getpass)) {


                    loginuser(getemail, getpass);
                }


            }
        });


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });


    }

    private void loginuser(String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                             String current_user = mAuth.getCurrentUser().getUid();
                            String devicetoken = FirebaseInstanceId.getInstance().getToken();

                            databaseReference.child(current_user).child("device_token").
                                    setValue(devicetoken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    // Sign in success, update UI with the signed-in user's information
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                    // we use flag in intent bcoz we accept a going back to home after  main page.
                                    finish();

                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });

    }
}
