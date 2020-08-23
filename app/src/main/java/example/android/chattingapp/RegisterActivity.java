package example.android.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mdisplayname;
    private TextInputLayout mEmail;
    private TextInputLayout mpassword;
    private EditText emailbutton;
    private EditText userbutton;
    private EditText passbutton;
    private Button msignup;
    private FirebaseUser firebaseUser;
    private DatabaseReference firebaseDatabase;
    private DatabaseReference databaseReference;
    public String user_email;
    public String user_username;
    public String user_pass;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    public static final Pattern EMAIL_Address = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + " ( " + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})\n");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = findViewById(R.id.Registrationtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Account");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, StartActivity.class));

            }
        });


        mdisplayname = findViewById(R.id.Activity_displayname);
        mEmail = findViewById(R.id.activity_inputlayout_Email);
        mpassword = findViewById(R.id.activity_password);


        emailbutton = findViewById(R.id.reg_email);
        userbutton = findViewById(R.id.reg_displayname);
        passbutton = findViewById(R.id.reg_password);
        msignup = findViewById(R.id.register_btn);


        msignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_email = emailbutton.getText().toString();
                user_username = userbutton.getText().toString();
                user_pass = passbutton.getText().toString();

                //if(TextUtils.isEmpty(user_username) || TextUtils.isEmpty(user_email) || TextUtils.isEmpty(user_pass))

                if (user_username.isEmpty()) {
                    userbutton.setError("This Field can't be Empty");
                }


                if (user_email.isEmpty()) {
                    // progressDialog.dismiss();
                    emailbutton.setError("Email is required");
                    emailbutton.requestFocus();

                } else if (!Patterns.EMAIL_ADDRESS.matcher(user_email).matches()) {
                    //progressDialog.dismiss();
                    emailbutton.setError("Enter a Valid Email Address");
                    emailbutton.requestFocus();
                }


                if (user_pass.isEmpty()) {
                    passbutton.setError("Password is required");
                    passbutton.requestFocus();

                }

                if (!TextUtils.isEmpty(user_username) || !TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_pass)) {

                    register_user(user_username, user_email, user_pass);

                }


            }
        });


    }

    private void register_user(final String username, String email, String password) {


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

                    String uid = current_user.getUid();

                    firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    String device_token = FirebaseInstanceId.getInstance().getToken();

                    HashMap<String, String> userMap = new HashMap<>();

                    userMap.put("name", username);
                    userMap.put("status", "Hii there I'm using Chatting App");
                    userMap.put("image", "default");
                    userMap.put("thumb_image", "default");
                    userMap.put("device_token", device_token);

                    firebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                startActivity(new Intent(RegisterActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                // we use flag in intent bcoz we accept a going back to home after  main page.
                                finish();


                            } else {
                                Toast.makeText(RegisterActivity.this, "Nikal Laude", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed ", Toast.LENGTH_SHORT).show();

                }


            }
        });


    }
}
