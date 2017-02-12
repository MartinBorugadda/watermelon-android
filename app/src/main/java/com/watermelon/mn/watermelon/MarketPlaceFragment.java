package com.watermelon.mn.watermelon;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MarketPlaceFragment extends Fragment {


    private DatabaseReference mDatabaseReference;
    private DatabaseReference marketplaceReference;
    private RecyclerView recyclerView;
    private OnListFragmentInteractionListener mListener;
    File productImageTempFile = null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MarketPlaceFragment() {
    }


    public static MarketPlaceFragment newInstance() {
        MarketPlaceFragment fragment = new MarketPlaceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        marketplaceReference = mDatabaseReference.child("marketplace");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_marketplace, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_product_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),AddProductActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseRecyclerAdapter<ProductInfo, ItemViewHolder> adapter = new FirebaseRecyclerAdapter<ProductInfo, ItemViewHolder>(ProductInfo.class,
                R.layout.recyclerview_item,ItemViewHolder.class, marketplaceReference ) {

            @Override
            protected void populateViewHolder(final ItemViewHolder viewHolder, ProductInfo model, final int position) {
                Log.d("Firebase download", model.title);
                viewHolder.mNameView.setText(model.title);
                viewHolder.mDescriptionView.setText(model.desc);
                viewHolder.mPriceView.setText( model.price);
                try{
                    String productPictureURL = model.productPictureURLs.get(0);
                    Log.d("ppURL", productPictureURL);
                    StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(productPictureURL);
                    pictureReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Got the download URL for 'users/me/profile.png'
                            // Pass it to Picasso to download, show in ImageView and caching
                            Log.d("Product Picture URI is", uri.toString());
                            Picasso.with(getContext()).load(uri).resize(75, 75).into(viewHolder.mProductImageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });


                }
                catch(Exception e){
                    Log.d("Exception", "Failed to fetch product Picture");
                }

//                 Get product picture in a native way
//                if(model.productPictureURLs!=null){
//                    StorageReference pictureReference = FirebaseStorage.getInstance().getReferenceFromUrl(model.productPictureURLs.get(0));
//                    try{
//                        productImageTempFile = File.createTempFile("images","jpg");
//                    }
//                    catch(IOException io){
//                        Log.d("Exception caught", io.toString());
//                    }
//
//                    pictureReference.getFile(productImageTempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            final BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inJustDecodeBounds = true;
//                            BitmapFactory.decodeFile(productImageTempFile.getPath(), options);
//
//                            options.inSampleSize = calculateInSampleSize(options, 75, 75);
//                            // Decode bitmap with inSampleSize set
//                            options.inJustDecodeBounds = false;
//                            Bitmap bitmap = BitmapFactory.decodeFile(productImageTempFile.getPath(), options);
//                            viewHolder.mProductImageView.setImageBitmap(bitmap);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    });
//                }


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != mListener) {
                            // Notify the active callbacks interface (the activity, if the
                            // fragment is attached to one) that an item has been selected.
                            mListener.onListFragmentInteraction(getRef(position));
                        }
                    }
                });

            }
        };
        recyclerView.setAdapter(adapter);

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

    public static class ItemViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        public final TextView mNameView;
        public final TextView mDescriptionView;
        public final TextView mPriceView;
        public final ImageView mProductImageView;

        public ItemViewHolder(View view){
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.item_name);
            mDescriptionView = (TextView) view.findViewById(R.id.item_description);
            mPriceView = (TextView) view.findViewById(R.id.item_price);
            mProductImageView = (ImageView) view.findViewById(R.id.item_icon);

        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(DatabaseReference ref);
    }
}
