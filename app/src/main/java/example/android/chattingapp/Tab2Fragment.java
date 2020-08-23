package example.android.chattingapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
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
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";
    private RecyclerView mFriendsList;
    private FirebaseAuth mAuth;
    private DatabaseReference frienddatabaseReference;
    private DatabaseReference databaseReference;
    private String mcurrent_user_id;
    private View mMainview;
    public Tab2Fragment() {


    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

      mMainview = inflater.inflate(R.layout.tab2fragment, container, false);

        mMainview = inflater.inflate(R.layout.tab3fragment, container, false);

        mFriendsList = mMainview.findViewById(R.id.friend_list);

        mAuth = FirebaseAuth.getInstance();


        mcurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        frienddatabaseReference = FirebaseDatabase.getInstance().getReference().child("Chat").child(mcurrent_user_id);
        frienddatabaseReference.keepSynced(true);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);


        mFriendsList.setHasFixedSize(true);

        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));




            Query query = FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Chat")
                    .child(mcurrent_user_id)
                    .limitToLast(20);

            FirebaseRecyclerOptions<Convers> options = new FirebaseRecyclerOptions.Builder<Convers>().
                    setQuery(query, Convers.class).build();

            FirebaseRecyclerAdapter<Convers, ConversViewHolder> fAdapter =
                    new FirebaseRecyclerAdapter<Convers, ConversViewHolder>(options){


                        @NonNull
                        @Override
                        public ConversViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_friends_layout, parent, false);

                            ConversViewHolder viewHolder = new ConversViewHolder(view);

                            return viewHolder;
                        }

                        @Override
                        protected void onBindViewHolder(@NonNull final ConversViewHolder holder, int position, @NonNull final Convers model) {

                            final String list_user_id = getRef(position).getKey();

                            databaseReference.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    String username = dataSnapshot.child("name").getValue(String.class);
                                    holder.naaam(username);

                                    String  img = dataSnapshot.child("image").getValue(String.class);

                                     holder.setimage(img);


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            holder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent chat_intent = new Intent(getContext(), ChatActivity.class);
                                    chat_intent.putExtra("user_id", list_user_id);
                                    startActivity(chat_intent);



                                }
                            });


                        }

                    };


            mFriendsList.setAdapter(fAdapter);

            fAdapter.startListening();



        return mMainview;
    }

    public static class ConversViewHolder extends RecyclerView.ViewHolder {


        View mview;

        public ConversViewHolder(View itemView) {
            super(itemView);

            mview = itemView;
        }

        public void naaam(String n)
        {
            TextView text = mview.findViewById(R.id.friends_singlename);
            text.setText(n);
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



