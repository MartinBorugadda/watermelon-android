package com.watermelon.mn.watermelon;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity{

    private FirebaseAuth mFirebaseAuth;
    private String TAG="tag";
    private LinearLayout signUpButton;
    private FloatingActionButton loginButton;
    private EditText userName;
    private EditText password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mFirebaseAuth = FirebaseAuth.getInstance();

        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (FloatingActionButton)findViewById(R.id.login_Button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userName.getText().toString()==null||userName.getText().toString().isEmpty()||password.getText().toString()==null||password.getText().toString()==null){
                    Toast.makeText(LoginActivity.this, "Enter valid username and password",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    signIn(userName.getText().toString(), password.getText().toString());
                }
            }
        });

        signUpButton = (LinearLayout) findViewById(R.id.loginSignUpLayout);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

    }

    private void signIn(String username, String password) {
        mFirebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authorization Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }

                        // ...
                    }
                });
    }



}
