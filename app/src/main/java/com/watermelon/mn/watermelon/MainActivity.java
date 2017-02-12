package com.watermelon.mn.watermelon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements MarketPlaceFragment.OnListFragmentInteractionListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;
    private String mUsername;
    private String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getEmail();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_main);

       /* Fragment universityEmailfragment = UniversityEmailFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.theFragmentFrame, universityEmailfragment).addToBackStack("UniversityEmailFragment").commit();*/

        Fragment fragment = new MarketPlaceFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.theFragmentFrame, fragment).addToBackStack("MarketPlaceFragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch(id){
            case R.id.logoff:
                FirebaseAuth.getInstance().signOut();
                Intent intent1=new Intent(this,LoginActivity.class);
                startActivity(intent1);
                return true;
            case R.id.profile:
                Intent intent2=new Intent(this,MyProfileActivity.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(DatabaseReference reference){
        Intent intent = new Intent(this, ProductDetailViewActivity.class);
        intent.putExtra("Product reference", reference.toString());
        startActivity(intent);
    }

}
