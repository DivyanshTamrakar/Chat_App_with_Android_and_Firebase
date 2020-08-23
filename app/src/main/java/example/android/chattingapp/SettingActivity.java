package example.android.chattingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.app.Activity.RESULT_OK;

import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.SiliCompressor;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    // Layout element declaration
    private CircularImageView profile_image;
    private TextView profile_name;
    private TextView profile_status;
    private Button change_image;
    private Button change_status;
    String download_url = null;
    private Uri mainImageuri = null;

    private static final int GALLERY_PICK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Account Setting");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
         toolbar.setNavigationOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 Intent intent = new Intent(SettingActivity.this, MainActivity.class);// New activity
                 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(intent);
                 finish();
             }
         });






        storageReference = FirebaseStorage.getInstance().getReference();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profile_image = findViewById(R.id.profimage);
        profile_name = (TextView) findViewById(R.id.profname);
        profile_status = findViewById(R.id.proos);
        change_image = findViewById(R.id.profchangeimage);
        change_status = findViewById(R.id.profchangestat);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid().toString());
        databaseReference.keepSynced(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String Name = dataSnapshot.child("name").getValue(String.class);
              final   String image = dataSnapshot.child("image").getValue(String.class);
                String tus = dataSnapshot.child("status").getValue(String.class);
                String thumb = dataSnapshot.child("thumb").getValue(String.class);


                profile_name.setText(Name);
                profile_status.setText(tus);


                if (!image.equals("default")) {


                    //Picasso.get().load(image).into(profile_image);// picassso online


                    // -------- picasso offiline capapbilit-------------

                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(SettingActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                            Picasso.get().load(image).into(profile_image);// picassso online
                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(SettingActivity.this, StatusActivity.class);
                intent.putExtra("prestatus", profile_status.getText().toString());
                startActivity(intent);


            }
        });

        change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Imagechooser();

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageuri = result.getUri();




                String current_uid = firebaseUser.getUid();


                final StorageReference ref = storageReference.child("picture.jpg").child(current_uid + ".jpg");



                ref.putFile(mainImageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        Toast.makeText(SettingActivity.this, "Profile photo saved", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;

                                download_url = downloadUrl.toString();

                                databaseReference.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(SettingActivity.this, "Succesfully Uploaded", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }
                        });

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }

    public void Imagechooser() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).
                setAspectRatio(1, 1).
                start(this);


    }


}
