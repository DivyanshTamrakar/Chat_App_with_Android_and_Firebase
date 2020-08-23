package example.android.chattingapp;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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

public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";
    private RecyclerView mFriendsList;
    private FirebaseAuth mAuth;
    private DatabaseReference frienddatabaseReference;
    private DatabaseReference databaseReference;
    private String mcurrent_user_id;
    private View mMainview;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mMainview = inflater.inflate(R.layout.tab3fragment, container, false);

        mFriendsList = mMainview.findViewById(R.id.friend_list);

        mAuth = FirebaseAuth.getInstance();


        mcurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        frienddatabaseReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(mcurrent_user_id);
        frienddatabaseReference.keepSynced(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);


        mFriendsList.setHasFixedSize(true);

        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        {


            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Friends")
                    .child(mcurrent_user_id)
                    .limitToLast(20);

            FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().
                    setQuery(query, Friends.class).build();

            FirebaseRecyclerAdapter<Friends, FriendsViewHolder> firebaseRecyclerAdapter =
                    new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {


                        @Override
                        protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {

                            holder.setDate(model.getDate());

                            final String list_user_id = getRef(position).getKey();

                            databaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String username = dataSnapshot.child("name").getValue(String.class);
                                    String user_image = dataSnapshot.child("image").getValue(String.class);

                                    if (dataSnapshot.hasChild("online")) {

                                        String user_signal = dataSnapshot.child("online").getValue().toString();
                                        holder.setSignal(user_signal);
                                    }


                                    holder.setName(username);
                                    holder.setimage(user_image);

                                    holder.mview.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            String[] options = {"Open Profile","Send Message"};// to show pop for selecting Options

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Select Option");
                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    if(which == 0){
                                                        Intent profile_intent = new Intent(getContext(), ProfileActivity.class);
                                                        profile_intent.putExtra("user_id", list_user_id);
                                                        startActivity(profile_intent);
                                                    }
                                                    if(which == 1){
                                                        Intent chat_intent = new Intent(getContext(), ChatActivity.class);
                                                        chat_intent.putExtra("user_id", list_user_id);
                                                        startActivity(chat_intent);

                                                    }

                                                }
                                            });
                                            builder.show();

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @NonNull
                        @Override
                        public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_friends_layout, parent, false);

                            FriendsViewHolder viewHolder = new FriendsViewHolder(view);

                            return viewHolder;
                        }
                    };


            mFriendsList.setAdapter(firebaseRecyclerAdapter);

            firebaseRecyclerAdapter.startListening();

        }
        return mMainview;

    }

   /* @Override
    protected void onStart()
*/

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {


        View mview;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }

        public void setDate(String date) {
            TextView userstatus = mview.findViewById(R.id.friends_single_status);
            userstatus.setText(date);

        }

        public void setName(String name) {
            TextView nameuser = mview.findViewById(R.id.friends_singlename);
            nameuser.setText(name);

        }

        public void setSignal(String active_status) {
            ImageView img = mview.findViewById(R.id.on_signal);
            if (active_status.equals("true")) {
                img.setVisibility(View.VISIBLE);
            } else {
                img.setVisibility(View.INVISIBLE);
            }


        }

        public void setimage(final String image) {
            final CircularImageView imgve = mview.findViewById(R.id.friends_userimage);

            if (!image.equals("default")) {
                //Picasso.get().load(image).into(imgview);

                Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(imgve, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso.get().load(image).into(imgve);// picassso online
                    }
                });
            }


        }

    }


}

