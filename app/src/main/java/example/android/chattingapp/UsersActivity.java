package example.android.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView mUsersList;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private ImageView toolimage;

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        change();


        toolbar = findViewById(R.id.active);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // getSupportActionBar().setTitle("All users");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UsersActivity.this, MainActivity.class);// New activity
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


        System.out.println("divyansh");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

// ------------ To get image on a toolbar------------- //
        toolimage = findViewById(R.id.tool_prof);
        String tool_image = firebaseUser.getUid();
        toolbar_photo(tool_image);
        toolimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UsersActivity.this,SettingActivity.class));

            }
        });


// --------------------------------------------------- //
        mUsersList = findViewById(R.id.UserRecycleView);

        mUsersList.setHasFixedSize(true);

        mUsersList.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .limitToLast(20);

        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>().
                setQuery(query, Users.class).build();

        FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {

                holder.setName(model.getName());
                holder.setStatus(model.getStatus());
                holder.setImage(model.getImage());


                final String user_id = getRef(position).getKey();// to get user id

                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profile_intent = new Intent(UsersActivity.this, ProfileActivity.class);
                        profile_intent.putExtra("user_id", user_id);
                        startActivity(profile_intent);


                    }
                });


            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);

                UserViewHolder viewHolder = new UserViewHolder(view);

                return viewHolder;
            }
        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {


        View mview;

        public UserViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }

        public void setName(String name) {
            TextView usernameview = mview.findViewById(R.id.singlename);
            usernameview.setText(name);
        }

        public void setStatus(String status) {
            TextView userstatus = mview.findViewById(R.id.single_status);
            userstatus.setText(status);
        }

        public void setImage(final String image) {
            final CircularImageView imgview = mview.findViewById(R.id.userimage);

            if (!image.equals("default")) {
                //Picasso.get().load(image).into(imgview);

                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imgview, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(image).into(imgview);// picassso online
                    }
                });
            }


        }
    }

    public void toolbar_photo(String tool_image) {
        // ------------ TO get image in the Toolbar----------

        databaseReference.child(tool_image).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String img = dataSnapshot.child("image").getValue(String.class);
                Picasso.get().load(img).into(toolimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void change() {
        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(UsersActivity.this, R.color.whitecolor1));
    }
}

