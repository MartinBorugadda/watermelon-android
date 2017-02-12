package com.watermelon.mn.watermelon;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;


public class UniversityEmailFragment extends Fragment {

    public UniversityEmailFragment() {
        // Required empty public constructor
    }

    public static UniversityEmailFragment newInstance() {
        UniversityEmailFragment fragment = new UniversityEmailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_university_email, container, false);
        final EditText universityEmailEnterTextField = (EditText) view.findViewById(R.id.email_entry_field);

        final String universityEmail = universityEmailEnterTextField.getEditableText().toString();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ImageButton updateEmailButton = (ImageButton) view.findViewById(R.id.proceed_icon);
        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(universityEmailEnterTextField.getText()!=null){
                    user.updateEmail(universityEmailEnterTextField.getText().toString());
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                            }
                        }
                    });
                }
            }
        });
        return view;
    }

}
