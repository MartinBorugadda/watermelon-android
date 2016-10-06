package com.watermelon.mn.watermelon;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyProfileActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userdataReference;
    private String userID;
    private UserProfileInfo userProfileInfo;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;
    private StorageReference userStorageReference;
    private File profileImage;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView profile_picture;
    private String mCurrentPhotoPath;
    private Uri profilePictureUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        userID = user.getUid();
        Log.d("USER ID", userID);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        userdataReference = mDatabaseReference.child("userdata");

        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReferenceFromUrl("gs://watermelon-8b360.appspot.com");
        userStorageReference = storageReference.child("UserStorage"+"/"+userID);


        setContentView(R.layout.activity_my_profile);

        final EditText profile_username = (EditText) findViewById(R.id.profile_name);
        final EditText profile_email = (EditText) findViewById(R.id.profile_email);
        final EditText profile_password = (EditText) findViewById(R.id.profile_password);
        final EditText profile_phone = (EditText)findViewById(R.id.profile_phone);
        final EditText profile_address = (EditText) findViewById(R.id.profile_address);
        profile_picture = (ImageView) findViewById(R.id.profile_picture);

        final StorageReference pictureReference = userStorageReference.child("profilePicture.jpg");
        try{
            profileImage = File.createTempFile("images","jpg");
        }
        catch(IOException io){
            Log.d("Exception caught", io.toString());
        }

        pictureReference.getFile(profileImage).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                profile_picture.setImageBitmap(BitmapFactory.decodeFile(profileImage.getAbsolutePath()));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });


        userdataReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userProfileInfo = dataSnapshot.getValue(UserProfileInfo.class);
                Log.d("username", userProfileInfo.username);
                profile_username.setText(userProfileInfo.username);
                Log.d("username", userProfileInfo.username);
                profile_email.setText(userProfileInfo.email);
                Log.d("email", userProfileInfo.email);
                profile_phone.setText(userProfileInfo.phone);
                Log.d("profile", userProfileInfo.phone);
                profile_address.setText(userProfileInfo.address);
                Log.d("address", userProfileInfo.address);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("Failure to read", "Failed to read value.", error.toException());
            }
        });

        Button submitButton = (Button) findViewById(R.id.profile_submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    if(profile_username.getText()!=null&&profile_email.getText()!=null&&profile_password.getText()!=null&&profile_phone.getText()!=null&&profile_address.getText()!=null){
                        userdataReference.child(user.getUid().toString()).setValue(new UserProfileInfo(profile_username.getText().toString(),profile_email.getText().toString(), profile_phone.getText().toString(), profile_address.getText().toString()));
                    }
                }
            }
        });


    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }
            catch(IOException io){
                Log.d("Exception caught", io.toString());
            }
            if(photoFile!=null){
                Uri photoUri = FileProvider.getUriForFile(this,"com.watermelon.mn.watermelon", photoFile);
                Log.d("URI is", photoUri.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            imageFromUri();
        }
        galleryAddPic();
    }

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        mCurrentPhotoPath = "file:"+ image.getAbsolutePath();
        profilePictureUri = Uri.fromFile(image);
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        /*File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);*/
        mediaScanIntent.setData(profilePictureUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void imageFromUri(){
        this.getContentResolver().notifyChange(profilePictureUri, null);
        ContentResolver cr = this.getContentResolver();
        Bitmap bitmap;
        try{
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, profilePictureUri);
            profile_picture.setImageBitmap(bitmap);
        }
        catch(IOException io){
            Log.d("IO Exception caught", io.toString());
        }
        StorageReference profilePictureRef = userStorageReference.child("profilePicture.jpg");
        UploadTask uploadTask = profilePictureRef.putFile(profilePictureUri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }
}
