package example.android.chattingapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;

    private FirebaseAuth mAuth;

    private DatabaseReference db;

    public MessageAdapter(List<Messages> mMessageList) {
        this.mMessageList = mMessageList;


    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);



        mAuth = FirebaseAuth.getInstance();//


        //db = FirebaseDatabase.getInstance().getReference().child("Users");

        //db.keepSynced(true);

        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

  //      mAuth = FirebaseAuth.getInstance();

        String current_user_idd = mAuth.getCurrentUser().getUid();


        Messages c = mMessageList.get(position);

        String fromUserID = c.getFrom();

        String from_message_type = c.getType();


        db  = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        db.keepSynced(true);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("image")){

                    String reciever_image = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(reciever_image).placeholder(R.drawable.blank).into(holder.imagecircle);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(from_message_type.equals("text")){


            holder.message_text.setVisibility(View.INVISIBLE);
            holder.sender.setVisibility(View.INVISIBLE);
            holder.imagecircle.setVisibility(View.INVISIBLE);

        if(fromUserID.equals(current_user_idd)){

            holder.sender.setVisibility(View.VISIBLE);
            holder.sender.setBackgroundResource(R.drawable.reciever_text_background);
            holder.sender.setTextColor(Color.BLACK);
            holder.sender.setText(c.getMessage());

        }else{



            holder.message_text.setVisibility(View.VISIBLE);
            holder.imagecircle.setVisibility(View.VISIBLE);


            holder.message_text.setBackgroundResource(R.drawable.message_text_background);
            holder.message_text.setTextColor(Color.BLACK);
            holder.message_text.setText(c.getMessage());




        }

        }



      /*
        if (from_user.equals(current_user_idd)) {


            holder.message_text.setBackgroundColor(Color.WHITE);
            holder.message_text.setTextColor(Color.BLACK);



        } else {

            holder.message_text.setBackgroundResource(R.drawable.message_text_background);
            holder.message_text.setTextColor(Color.WHITE);

        }*/

        holder.message_text.setText(c.getMessage());


    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView message_text;
        public CircularImageView imagecircle;
        public TextView sender;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);


            message_text = itemView.findViewById(R.id.single_message_text);
            imagecircle = itemView.findViewById(R.id.single_message_image);
              sender = itemView.findViewById(R.id.sender_message_text);

        }


    }

}
