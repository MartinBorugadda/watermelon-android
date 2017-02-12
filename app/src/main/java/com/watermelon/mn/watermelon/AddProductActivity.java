package com.watermelon.mn.watermelon;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.watermelon.mn.watermelon.MyProfileActivity.REQUEST_IMAGE_CAPTURE;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseReference marketplaceReference;
    private DatabaseReference currentProductReference;
    private DatabaseReference productImageURLsReference;


    private StorageReference storageReference;
    private StorageReference productImagesStorageReference;

    private FirebaseUser mFirebaseUser;
    private String userID;

    private Uri productPictureUri;


    private String currentKey;
    private String imageFileName;
    private List<String> productPictureURLList = new ArrayList<String>();
    private List<Uri> productImageLocalURIs = new ArrayList<Uri>();

    private ProductImagesRecyclerViewAdapter productImagesRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        marketplaceReference = FirebaseDatabase.getInstance().getReference().child("marketplace");

        currentProductReference=marketplaceReference.push();
        currentKey=currentProductReference.getKey();

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = mFirebaseUser.getUid();

        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://watermelon-8b360.appspot.com");
        productImagesStorageReference = storageReference.child("ProductImages"+"/"+currentKey);

        final EditText title = (EditText)findViewById(R.id.add_product_title);
        final EditText desc = (EditText)findViewById(R.id.add_product_description);
        final EditText price = (EditText) findViewById(R.id.add_product_price);

        productImagesRecyclerViewAdapter = new ProductImagesRecyclerViewAdapter();
        final RecyclerView productImagesRecyclerView = (RecyclerView) findViewById(R.id.add_product_images_recycler_view);
        productImagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        productImagesRecyclerView.setAdapter(productImagesRecyclerViewAdapter);

        final ImageView addProductImagePlusIcon = (ImageView) findViewById(R.id.add_product_image_plus_icon);
        addProductImagePlusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        Button submitButton = (Button) findViewById(R.id.add_product_submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentProductReference.setValue(new ProductInfo(title.getText().toString(), desc.getText().toString(), price.getText().toString(), "yes", productPictureURLList, userID));
                imageFromUri();
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            productImageLocalURIs.add(productPictureUri);
            //productImagesRecyclerViewAdapter.notifyItemInserted(productImageLocalURIs.size()-1);
        }
    }

    @Override
    public void onStart(){
        super.onStart();
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

    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir );
        productPictureUri = Uri.fromFile(image);
        return image;
    }

    public void imageFromUri(){
        for(Uri proPicUri:productImageLocalURIs){
            this.getContentResolver().notifyChange(proPicUri, null);
            ContentResolver cr = this.getContentResolver();
            StorageReference pictureRef = productImagesStorageReference.child(proPicUri.getLastPathSegment());
            UploadTask uploadTask = pictureRef.putFile(proPicUri);
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
            productPictureURLList.add(pictureRef.toString());
            currentProductReference.child("productPictureURLs").setValue(productPictureURLList);
        }
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ImageView productImage;

        public ItemViewHolder(View v){
            super(v);
            view=v;
            productImage = (ImageView) v.findViewById(R.id.product_image_in_card);
        }

        public void bindImage(Uri imageUri){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageUri.getPath(), options);

            options.inSampleSize = calculateInSampleSize(options, 90, 90);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
            productImage.setImageBitmap(bitmap);
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }
            return inSampleSize;
        }
    }

    public class ProductImagesRecyclerViewAdapter extends RecyclerView.Adapter<ItemViewHolder>{

        @Override
        public int getItemCount(){
            return productImageLocalURIs.size();
        }

        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_card_addproductimage, parent, false);
            ItemViewHolder vh = new ItemViewHolder(view) ;
            return vh;
        }

        public void onBindViewHolder(ItemViewHolder vh, int position){
            Uri imageUri = productImageLocalURIs.get(position);
            vh.bindImage(imageUri);
        }
    }


}
