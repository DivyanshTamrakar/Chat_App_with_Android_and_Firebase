package example.android.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    private TextInputLayout lay;
    private TextInputEditText edttext;
    private Button btn;

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        changestatusbarColor();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving Changes");
        progressDialog.setMessage("Please wait while we are saving your status");
        progressDialog.setCancelable(false);


        lay = findViewById(R.id.Tam);
        edttext = findViewById(R.id.writestatus);
        btn  = findViewById(R.id.status_save_btn);


        String sta = getIntent().getStringExtra("prestatus");
        edttext.setText(sta);


             firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();

             //String uid = firebaseUser.getUid();

             databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid().toString());





    btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            progressDialog.show();

             String updatestatus = edttext.getText().toString();

             databaseReference.child("status").setValue(updatestatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     if(task.isSuccessful())
                     {
                         progressDialog.dismiss();


                         new AlertDialog.Builder(StatusActivity.this).
                                 setTitle("Changes Saved").
                                 setMessage("Your Status is Updated").
                                 setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(StatusActivity.this,SettingActivity.class));
                                        finish();
                                     }
                                 }).
                                 setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                     @Override
                                     public void onClick(DialogInterface dialog, int which) {
                                         dialog.dismiss();
                                     }
                                 }).
                                 setCancelable(false).show();

                     }


                 }
             });

        }
    });



    }





    public void changestatusbarColor(){
        Window window = this.getWindow();



// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(StatusActivity.this,R.color.statuscolor));





    }


}
