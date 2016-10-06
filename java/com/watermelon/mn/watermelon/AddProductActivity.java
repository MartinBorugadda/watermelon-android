package com.watermelon.mn.watermelon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private DatabaseReference marketplaceReference;
    private DataSnapshot marketplaceDataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        marketplaceReference = mDatabaseReference.child("marketplace");
        final EditText title = (EditText)findViewById(R.id.add_product_title);
        final EditText desc = (EditText)findViewById(R.id.add_product_description);
        final EditText price = (EditText) findViewById(R.id.add_product_price);
        Button submitButton = (Button) findViewById(R.id.add_product_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long numberOfChildren= marketplaceDataSnapshot.getChildrenCount();
                DataSnapshot lastChild = null;
                for(DataSnapshot snapshot: marketplaceDataSnapshot.getChildren()){
                    lastChild = snapshot;
                }
                if(lastChild!=null){
                    Integer currentKey = Integer.parseInt(lastChild.getKey());
                    currentKey++;
                    if(title.getText()!=null&&desc.getText()!=null&&price.getText()!=null)
                    marketplaceReference.child(currentKey.toString()).setValue(new ProductInfo(title.getText().toString(), desc.getText().toString(), price.getText().toString(), "yes"));
                }
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        marketplaceReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                marketplaceDataSnapshot= dataSnapshot;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
