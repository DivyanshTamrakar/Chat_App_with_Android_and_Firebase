package example.android.chattingapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";
    private RecyclerView mRequestList;
    private FirebaseAuth mAuth;
    private DatabaseReference friendrequestdatabaseReference;
    private DatabaseReference databaseReference;
    private String mcurrent_user_id;
    private View mMainview;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMainview = inflater.inflate(R.layout.tab1fragment, container, false);
        mRequestList = mMainview.findViewById(R.id.request_list);

        mAuth = FirebaseAuth.getInstance();

        mcurrent_user_id = mAuth.getCurrentUser().getUid();

        friendrequestdatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mcurrent_user_id);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");


        mRequestList.setHasFixedSize(true);

        mRequestList.setLayoutManager(new LinearLayoutManager(getContext()));


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Friend_req")
                .child(mcurrent_user_id)
                .limitToLast(20);

        FirebaseRecyclerOptions<request> options = new FirebaseRecyclerOptions.Builder<request>().
                setQuery(query, request.class).build();

        FirebaseRecyclerAdapter<request, RequestViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<request, RequestViewHolder>(options) {


                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_layout, parent, false);

                        RequestViewHolder viewHolder = new RequestViewHolder(view);

                        return viewHolder;


                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull final request model) {

                        //  holder.settype(model.getRequest_type());

                        final String list_user_id = getRef(position).getKey();

                        databaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                String username = dataSnapshot.child("name").getValue(String.class);
                                String user_image = dataSnapshot.child("image").getValue(String.class);

                                holder.setName(username);
                           //     holder.setimage(user_image);


                                holder.mview.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                    }
                                });


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                };
        mRequestList.setAdapter(firebaseRecyclerAdapter);

        firebaseRecyclerAdapter.startListening();


        return mMainview;
    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {


        View mview;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }


        public void setName(String nname) {
            TextView nameuser = mview.findViewById(R.id.request_name);
            nameuser.setText(nname);

        }

        public void setimage(final String image) {
            final CircularImageView imgve = mview.findViewById(R.id.friends_userimage);

            if (!image.equals("default")) {
                //Picasso.get().load(image).into(imgview);
                Picasso.get().load(image).into(imgve);// picassso online


            }
            else{
                System.out.println("name : " + "akaks");

            }


        }
    }


}

