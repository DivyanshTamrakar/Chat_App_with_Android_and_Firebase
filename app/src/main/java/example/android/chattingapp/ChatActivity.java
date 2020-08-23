package example.android.chattingapp;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar chat_tool;
    private TextView text_username;
    private DatabaseReference mRoot;
    private FirebaseAuth mAuth;
    private String currentUserId;

    private CircularImageView friend_tool_circle;
    private TextView last_seen;
    private static final int TOTAL_ITEM_LOAD = 10;
    private int current_page = 1;
    private SwipeRefreshLayout layoutswipe;
    // ---------Messaging Component =-------

    private ImageButton message_media;
    private EditText message_message;
    private ImageButton message_send;


    private RecyclerView mMessageList;

    private final List<Messages> mlist = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mChatUser = getIntent().getStringExtra("user_id");


        mRoot = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        currentUserId = mAuth.getCurrentUser().getUid();

        chat_tool = findViewById(R.id.tool_chat);
        text_username = findViewById(R.id.text_username);
        last_seen = findViewById(R.id.text_user_lastseen);


        //  =================== Message tools ========================     //

        message_media = findViewById(R.id.message_media);
        message_message = findViewById(R.id.message_message);
        message_send = findViewById(R.id.message_send);


        friend_tool_circle = findViewById(R.id.tool_user_image);

        setSupportActionBar(chat_tool);

        getSupportActionBar().setDisplayShowTitleEnabled(false);


        mAdapter = new MessageAdapter(mlist);


        mMessageList = findViewById(R.id.chat_recycle);
        layoutswipe = findViewById(R.id.swipe_refresh);

        mLinearLayout = new LinearLayoutManager(ChatActivity.this);

        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mAdapter);

        loadMessages();


        mRoot.child("Users").child(mChatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue(String.class);
                text_username.setText(name);
                String img = dataSnapshot.child("image").getValue(String.class);
                if (!img.equals("default")) {

                    Picasso.get().load(img).into(friend_tool_circle);
                }

                String online = dataSnapshot.child("online").getValue().toString();

                if (online.equals("true")) {

                    last_seen.setText("Online");

                } else {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();


                    long last_time = Long.parseLong(online);

                    String last_seen_time = getTimeAgo.getTimeAgo(last_time, ChatActivity.this);


                    last_seen.setText(last_seen_time);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        // ===============  Chatting feature in databse ========================


        mRoot.child("Chat").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mChatUser)) {

                    Map chat_add_map = new HashMap();

                    chat_add_map.put("seen", false);
                    chat_add_map.put("timestamp", ServerValue.TIMESTAMP);


                    Map chat_user_map = new HashMap();

                    chat_user_map.put("Chat/" + currentUserId + "/" + mChatUser, chat_add_map);
                    chat_user_map.put("Chat/" + mChatUser + "/" + currentUserId, chat_add_map);

                    mRoot.updateChildren(chat_user_map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("Chat_Log", databaseError.getMessage().toString());
                            }


                        }
                    });

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        message_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendMessage();
                message_message.setText(" ");

            }
        });


        layoutswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                 current_page++;
                 mlist.clear();
                 loadMessages();
                 //load_more_Messages();
            }
        });

    }

    private void load_more_Messages() {
    }

    private void loadMessages() {


        DatabaseReference messageRef = mRoot.child("messages").child(currentUserId).child(mChatUser);

        Query messagequery  = messageRef.limitToLast(current_page * TOTAL_ITEM_LOAD);

    messagequery.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            Messages message = dataSnapshot.getValue(Messages.class);
            mlist.add(message);
            mAdapter.notifyDataSetChanged();


             mMessageList.scrollToPosition(mlist.size() - 1);
             layoutswipe.setRefreshing(false);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });



    }

    private void sendMessage() {

        String message = message_message.getText().toString();

        if (!TextUtils.isEmpty(message)) {


            String current_user_ref = "messages/" + currentUserId + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + currentUserId;


            DatabaseReference user_message_push = mRoot.child("messages").child(currentUserId).child(mChatUser).push();


            String user_push_id = user_message_push.getKey();

            Map messageMap = new HashMap();

            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from",currentUserId);


            Map message_user_map = new HashMap();

            message_user_map.put(current_user_ref + "/" + user_push_id, messageMap);
            message_user_map.put(chat_user_ref + "/" + user_push_id, messageMap);

            mRoot.updateChildren(message_user_map, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if (databaseError != null) {

                        Log.d("Chat_Log", databaseError.getMessage().toString());
                    }

                }
            });


        }


    }
}
