package example.android.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaeger.library.StatusBarUtil;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView user_profile_name, user_profile_status, Totalfriends;
    private Button user_send_request;
    private Button user_decline_btn;
    private ProgressDialog progressDialog;
    private FirebaseUser mcurrent_user;
    private String current_state;

    private DatabaseReference databaseReference;
    private DatabaseReference friendrequestdatabase;
    private DatabaseReference frienddatabaseReference;
    private DatabaseReference notificationdatabse ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        change();


        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ProfileActivity.this, UsersActivity.class);// New activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        final String user_id = getIntent().getStringExtra("user_id");


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);


        friendrequestdatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        friendrequestdatabase.keepSynced(true);
        frienddatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends");
        frienddatabaseReference.keepSynced(true);
        notificationdatabse = FirebaseDatabase.getInstance().getReference().child("notifications");
        notificationdatabse.keepSynced(true);


        mcurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        profile_image = findViewById(R.id.user_profile_image);
        user_profile_name = findViewById(R.id.user_profile_display_name);
        user_profile_status = findViewById(R.id.user_profile_current_status);
        Totalfriends = findViewById(R.id.user_profile_friends);
        user_send_request = findViewById(R.id.user_profile_send_request);
        user_decline_btn = findViewById(R.id.user_profile_decline_request);


        current_state = "not_friends";

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading User Data...");
        progressDialog.setMessage("Please wait while we are load the user data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue(String.class);
                String display_profile_image = dataSnapshot.child("image").getValue(String.class);
                String dispaly_status = dataSnapshot.child("status").getValue(String.class);


                user_profile_name.setText(display_name);
                user_profile_status.setText(dispaly_status);
                if (!display_profile_image.equals("default")) {

                    Picasso.get().load(display_profile_image).into(profile_image);
                }

                // ----------------------------FriendList/ Accept feature--------------------------

                friendrequestdatabase.child(mcurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue(String.class);
                            if (req_type.equals("Recieved")) {

                                current_state = "req_received";
                                user_send_request.setText("Accept Friend Request");

                                user_decline_btn.setVisibility(View.VISIBLE);
                                user_decline_btn.setEnabled(true);

                            } else if (req_type.equals("sent")) {

                                current_state = "req_sent";
                                user_send_request.setText("Cancel Friend Request");

                                user_decline_btn.setVisibility(View.INVISIBLE);
                                user_decline_btn.setEnabled(false);

                            }

                        } else {


                            frienddatabaseReference.child(mcurrent_user.getUid()).child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {
                                        current_state = "friends";
                                        user_send_request.setText("Unfriend this person");


                                        user_decline_btn.setVisibility(View.INVISIBLE);
                                        user_decline_btn.setEnabled(false);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        user_send_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_send_request.setEnabled(false);


                // -------------------------Not friend state-------------------------------


                if (current_state.equals("not_friends")) {

                    friendrequestdatabase.child(mcurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                 friendrequestdatabase.child(user_id).child(mcurrent_user.getUid())
                                        .child("request_type").setValue("Recieved").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        HashMap<String,String> notification_data = new HashMap<String, String>();
                                        notification_data.put("from",mcurrent_user.getUid());
                                        notification_data.put("type","request");


                                        notificationdatabse.child(user_id).push().setValue(notification_data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                user_send_request.setEnabled(true);
                                                current_state = "req_sent";
                                                user_send_request.setText("Cancel Request");
                                                user_decline_btn.setVisibility(View.INVISIBLE);
                                                user_decline_btn.setEnabled(false);


                                            }
                                        });


                                        //Toast.makeText(ProfileActivity.this, "Request sent succesfully", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            } else {
                                user_send_request.setEnabled(true);
                                Toast.makeText(ProfileActivity.this, "Failed to Sending Request", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

                // -------------- Cancel Request -----------------

                if (current_state.equals("req_sent")) {

                    friendrequestdatabase.child(mcurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            friendrequestdatabase.child(user_id).child(mcurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    user_send_request.setEnabled(true);
                                    current_state = "not_friends";
                                    user_send_request.setText("Send Friend Request");
                                    user_decline_btn.setVisibility(View.INVISIBLE);
                                    user_decline_btn.setEnabled(false);
                                }
                            });
                        }
                    });

                }

                // --------------------- Request Received state ----------------------------


                if (current_state.equals("req_received")) {

                    final String current_date = DateFormat.getDateTimeInstance().format(new Date());

                    frienddatabaseReference.child(mcurrent_user.getUid()).child(user_id).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            frienddatabaseReference.child(user_id).child(mcurrent_user.getUid()).child("date").setValue(current_date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    friendrequestdatabase.child(mcurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            friendrequestdatabase.child(user_id).child(mcurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    user_send_request.setEnabled(true);
                                                    current_state = "friends";
                                                    user_send_request.setText("Unfriend this Person");
                                                    user_decline_btn.setVisibility(View.INVISIBLE);
                                                    user_decline_btn.setEnabled(false);
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                        }
                    });


                }


            }
        });


    }
    public void change(){
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(ProfileActivity.this,R.color.greycolor));
    }


}
