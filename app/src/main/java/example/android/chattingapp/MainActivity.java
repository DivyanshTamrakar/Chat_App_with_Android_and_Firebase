package example.android.chattingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private long backPressedTime;
    private Toast backToast;


    private sectionsPageAdapter mSectionPageAdapter;
    private ViewPager mviewPager;
    private DatabaseReference User_Ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isOnline();

         //  ---------Initialize Firebase Auth----------------

        mAuth = FirebaseAuth.getInstance();

        //   ----------Initialize FirebaseDatabase-------------------

        if(mAuth.getCurrentUser()!=null){


            User_Ref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid().toString());
        }




        mSectionPageAdapter = new sectionsPageAdapter(getSupportFragmentManager());

        mviewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(mviewPager);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mviewPager);



        Toolbar toolbar = findViewById(R.id.hometoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ChatApp");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("MyNotifications", "MyNotifications",NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser(); // rtuen null when user is ot logined

        if (currentUser == null) {

            startActivity(new Intent(MainActivity.this, StartActivity.class));

            finish();// On back button we would not go to login activity again.

        } else {

            User_Ref.child("online").setValue("true");
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser(); // rtuen null when user is ot logined
        if(currentUser != null){

            User_Ref.child("online").setValue(ServerValue.TIMESTAMP);
            //User_Ref.child("lastseen").setValue(ServerValue.TIMESTAMP);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the payment_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.Logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                Toast.makeText(this, "You are Successfully Logged out", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.search:
                Toast.makeText(this, "create what to do", Toast.LENGTH_SHORT).show();
                ;
                return true;
            case R.id.settingaccount:


                startActivity(new Intent(MainActivity.this, SettingActivity.class));

                return true;
            case R.id.userall:

                startActivity(new Intent(MainActivity.this, UsersActivity.class));


                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setupViewPager(ViewPager viewPager) {

        sectionsPageAdapter adapter = new sectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new Tab1Fragment(), "Request");
        adapter.addFragment(new Tab2Fragment(), "Chats");
        adapter.addFragment(new Tab3Fragment(), "Friends");
        viewPager.setAdapter(adapter);


    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
